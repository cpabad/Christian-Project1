# monitoring/ — Uptime Kuma, the decoupled heartbeat monitor

This directory runs [Uptime Kuma](https://github.com/louislam/uptime-kuma) as the box's
synthetic monitor: it polls the monolith's `/health` endpoint (and Jenkins) on a schedule,
records the uptime history, and shows at a glance whether the application has been stable
for the last hours — the production-support view, as opposed to Jenkins' "did the last
change build" view.

## Why a separate monitor for a monolith

A monolith can (and in serious shops does) have decoupled observability. The monitor's one
non-negotiable property is that it must **never share fate with what it monitors**:

- It is NOT deployed inside Tomcat (Tomcat down would take the monitor down with it).
- It is NOT a Jenkins job (Jenkins is itself one of the things being watched).
- It is NOT in the K3s cluster (the cluster is another future monitoring target).

It is a host-level Docker Compose service with its own restart policy and its own data
volume. The existing watchdog cron remains the monitor-of-the-monitor: cron is the most
boring, hardest-to-kill scheduler on the box.

## Setup

```
cd monitoring
docker compose up -d
```

Open http://127.0.0.1:3001 (loopback-only, same stance as Jenkins — the dashboard is for
this machine's owner, not the network). The first visit asks you to create the admin
account; Kuma stores it in its own volume, no secrets live in this repo.

### Monitors to add (via the UI)

| Monitor | Type | URL | Success condition |
|---|---|---|---|
| ERS monolith | HTTP(s) - Keyword | `http://localhost:8080/ReimbursementManagement/health` | Keyword `UP` found, status 200 |
| Jenkins | HTTP(s) | `http://127.0.0.1:8090/login` | Status 200 |

Notes:
- Plain `localhost` works because the container runs with `network_mode: host` — it shares
  the host's network stack. This is not laziness: ufw on this box drops traffic from docker
  bridge networks to the host (verified — `host.docker.internal` times out on every
  gateway), so a bridged monitor could never poll host Tomcat, and Jenkins' port is
  loopback-only on top of that. Host networking sidesteps both, and
  `UPTIME_KUMA_HOST=127.0.0.1` keeps the dashboard itself loopback-only.
- The keyword monitor is the right type for `/health`: the endpoint answers 200 with body
  `{"status":"UP"}` only when the app can reach Postgres, and 503 `{"status":"DOWN"}` when
  it cannot — so "keyword UP + status 200" distinguishes app-up-DB-down from healthy.
- A 60-second interval is plenty here; the endpoint opens and closes one JDBC connection
  per probe by design.

## The same idea in cloud terms

The `/health` endpoint speaks the exact contract managed platforms consume; only the
poller changes:

- **Kubernetes liveness/readiness probes** — the kubelet polls each container's health
  endpoint. Liveness failure -> restart the container; readiness failure -> stop routing
  traffic to it. This is the K8s analog of what Kuma does here (K3s already uses probes for
  the auth slice).
- **AWS ECS health checks** — same pattern: the task definition names a health command/URL,
  and ECS replaces tasks that fail it.
- **Route 53 health checks** — DNS-level: an endpoint failing its check is pulled out of
  DNS answers (usually for failover between regions/instances).

On load balancing, without paying for one:

- A Kubernetes **Service** is a built-in L4 load balancer: it spreads connections across
  every ready pod behind it, for free, inside the cluster.
- The **Traefik Ingress** that ships with K3s is the free L7 (HTTP-aware) balancer in
  front of Services — path/host routing, no cloud LB bill.
- The **HorizontalPodAutoscaler** scales *pods* (more copies of the app on the same
  machines) when load rises; it does not add machines — that would be cluster autoscaling
  on a cloud provider, which is where the bill starts.

So: Kuma answers "has my app been healthy for the last hours" on this box today, and the
`/health` endpoint it polls is already shaped for probes, ECS, or Route 53 the day the app
runs there.

## Operations

```
docker compose ps            # is the monitor itself up
docker compose pull && docker compose up -d    # upgrade, history survives (named volume)
docker compose down          # stop (volume kuma-data keeps all history/config)
```
