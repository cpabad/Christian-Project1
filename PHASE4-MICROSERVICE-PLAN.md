# Phase 4 - Refactor ERS into a (Spring Boot) Microservice

Blueprint for evolving ERS from a standalone-Tomcat WAR (servlets + hand-written
Hibernate DAOs + `HttpSession` filters) into a Spring Boot service, adopting the
proven technologies from Project2 (Spring Boot / Spring Data / Angular) and Project3
(the "Chronicle" reference: containerized Spring Boot + JWT auth + externalized storage).

Guiding principle: **do this on a parallel branch and keep the working WAR + 97 green
tests intact as the reference state until the Spring Boot version reaches parity.** No
big-bang rewrite; migrate in verifiable slices.

---

## 1. What "microservice" actually means (and the 4 "ingredients")

A service is a *microservice* if it is **independently deployable, scoped to one business
capability, and owns its own data**, communicating over the network. That's the definition.

Service **discovery (Eureka)**, **API gateway (Spring Cloud Gateway)**, **load balancing
(Spring Cloud LoadBalancer; Ribbon is EOL)**, and **circuit breaker (Resilience4j; Hystrix
is EOL)** are NOT the definition - they are the supporting infrastructure for running a
*fleet* of services at scale. A single service needs none of them.

**Determination - is Chronicle (Project3) a microservices architecture?** No. It is one
Spring Boot backend + one Angular SPA + external Firebase (auth) + external S3 (storage) +
H2. It has none of the four components, correctly, because there is only one service.

**Implication for ERS:** adding the four components to a single-service ERS is
over-engineering - decorative infrastructure with no real job. They become meaningful only
after ERS is decomposed into 2+ services. See the optional learning track (Section 7).

---

## 2. Target stack (adopted from P2 + P3)

| Concern | From | Target |
| --- | --- | --- |
| Runtime | P3 | Spring Boot fat JAR + embedded Tomcat, Dockerized |
| Web | P2/P3 | `@RestController` + annotation routing (replaces `FrontController`/`RequestHelper`) |
| Data | P2/P3 | Spring Data JPA interfaces (replaces hand-written `*RepositoryImpl`) |
| Models | P3 | JPA `@Entity` + Lombok `@Data` (cuts getter/setter/ctor boilerplate) |
| Auth | P3 idea, ERS-native | Spring Security + **self-issued JWT** (keep ERS's own user table + BCrypt; no Firebase) |
| Config | P3 | `application.properties` + dev/prod profiles (replaces `hibernate.cfg.xml`) |
| Logging | already done | Log4j via `LOG.error` (Phase 2) |
| Deploy | P3 + your AWS DevOps | Dockerfile; optionally docker-compose (api + db [+ spa]) |

---

## 3. The quick win - data layer (Impl -> Spring Data)

This is the fastest, highest-payoff slice: ~15 hand-written `*RepositoryImpl` classes (plus
their interfaces) collapse into ~15 one-line Spring Data interfaces. Spring generates the
implementation; custom finders become derived queries or `@Query`. The `printStackTrace`
sweep, `HibernateSessionFactory`, `ConnectionFactory`, and per-method session/transaction
boilerplate all disappear (the framework owns them).

**Before** (`RequestRepositoryImpl`, ~185 lines of try/catch/session/commit per method):

```java
public class RequestRepositoryImpl implements RequestRepository {
    public Request findById(int id) {
        Session s = null; Transaction tx = null; Request request = null;
        try { s = HibernateSessionFactory.getSession(); tx = s.beginTransaction();
              request = s.createQuery("FROM Request r WHERE r.requestId = :id", Request.class)
                         .setParameter("id", id).getSingleResult();
              tx.commit();
        } catch (HibernateException e) { LOG.error("...", e); tx.rollback(); }
        finally { s.close(); }
        return request;
    }
    // ... 8 more methods, same shape ...
}
```

**After** (one interface; `findById`/`findAll`/`save`/`delete` are inherited):

```java
@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByRequester(User requester);
    List<Request> findByRequesterAndRequestStatus_StatusId(User requester, int statusId);
    Request findByEventDateAndEventLocationAndRequester(Date d, EventLocation loc, User requester);
}
```

Services change from `new RequestRepositoryImpl()` to constructor-injected
`RequestRepository`; method names line up so service logic is largely untouched.

**Prerequisite (the honest part):** Spring Data needs a Spring context. So the quick win
rides on a Spring Boot scaffold (new `pom.xml` with `spring-boot-starter-data-jpa`, an
`@SpringBootApplication`, `application.properties` datasource). That scaffold is step 0;
the repo collapse is step 1 on top of it.

Effort: scaffold ~half day; data-layer collapse ~2-3h once scaffolded. **[quick win]**

---

## 4. Auth - self-issued JWT + Spring Security (your JWT goal)

Chronicle uses Firebase as the JWT issuer; ERS already owns a user table with BCrypt, so it
should **issue its own JWTs** (no external IdP needed):

1. `POST /login` verifies username/password with `BCryptPasswordEncoder.matches`, then mints
   an ERS-signed JWT (subject = user id, claim = role).
2. Spring Security validates the JWT on every request (a `OncePerRequestFilter` or resource-
   server config), populating the `SecurityContext`.
3. The three servlet filters (`SessionFilter` / `EmployeeFilter` / `ManagerFilter`) collapse
   into Spring Security config: `authorizeRequests()` + `@PreAuthorize("hasRole('MANAGER')")`
   on manager-only endpoints.

This is the main learning slice and the biggest behavior change. Effort: ~1-2 days. **[grind]**

---

## 5. Sequencing (verifiable slices, parallel branch)

0. Branch `christian/microservice`; scaffold Spring Boot, keep WAR intact.  ~half day
1. Port models (add Lombok, move mapping to `application.properties`); prove app boots.  ~2-3h
2. **Quick win:** convert DAOs to Spring Data interfaces; wire services via DI.  ~2-3h  [quick win]
3. Add `@RestController`s mirroring current endpoints; return `ResponseEntity`.  ~half day
4. Auth: self-issued JWT + Spring Security; retire the servlet filters.  ~1-2 days  [grind]
5. Port tests: `@DataJpaTest` (repos), `@WebMvcTest` (controllers), keep Mockito service tests.  ~half day
6. Dockerfile (+ optional docker-compose: api + Postgres).  ~2-3h
7. (Optional) separate the frontend into its own SPA + nginx container.  ~1 day

Checkpoint after each slice; the app must build + tests pass before the next.

---

## 6. What NOT to copy from Chronicle

Training-project smells observed in P3, to avoid importing: `cors.allowed-origins=*` WITH
`allow-credentials=true` (spec-invalid + insecure), `show_sql=true`, `System.out.println` in
services, `ddl-auto=create` on in-memory H2 (wipes data each boot), and redundant double
token verification. ERS on real Postgres with hashed passwords is already more
production-honest on several of these.

---

## 7. OPTIONAL learning track - a real microservices architecture

Only pursue if the goal is to *learn* the Spring Cloud stack, not because a single ERS needs
it. To make the four components meaningful, first **decompose ERS into 2+ services** (e.g.
`user-auth-service` and `reimbursement-service`, each with its own data), then add:

- **Service discovery:** Spring Cloud Netflix Eureka (or Consul).
- **API gateway:** Spring Cloud Gateway (single entry point, routing, cross-cutting auth).
- **Load balancing:** Spring Cloud LoadBalancer (Ribbon is EOL); in AWS this is often the ALB/NLB.
- **Circuit breaker:** Resilience4j (Hystrix is EOL).

Caveat to keep in view: this multiplies operational complexity (multiple deployables, inter-
service contracts, distributed data). It is a strong *learning* exercise and resume signal;
it is not justified by ERS's actual scale. Treat it as a deliberate study project, clearly
separate from the core migration above.

Effort: ~2-4 days on top of the core migration.  [grind / learning]
