package com.revature.util;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 * Request-path narration for re-learning the data flow. Writes hop-numbered lines through the
 * dedicated FLOW logger, which is console-only (see log4j2.xml - no file appender, the trace
 * vanishes on process exit). Toggle: the FLOW logger's level - DEBUG = on, OFF = silent; the
 * test configuration (log4j2-test.xml) ships with OFF so test output stays clean.
 *
 * A "frame" spans one HTTP request: the FIRST filter in the chain calls begin() and owns the
 * frame; end() runs in its finally so pooled Tomcat threads never carry a stale request id into
 * the next request. Forwards (RequestDispatcher) stay on the same thread, so a whole forward
 * chain shares one request id. Every line carries [reqId|hop]; hop increments per line, so one
 * request's narrative reads in order even when lines from concurrent requests interleave.
 *
 * Redaction rule (hard constraint): events passed to log() must never contain passwords,
 * hashes, session tokens, or cookie values. Usernames are acceptable.
 */
public final class FlowTrace {

	private static final Logger LOG = LogManager.getLogger("FLOW");
	private static final String REQ_ID_KEY = "flowReqId";
	private static final ThreadLocal<int[]> HOP = ThreadLocal.withInitial(() -> new int[1]);

	private FlowTrace() {
	}

	/**
	 * Opens a request frame if none is active on this thread. Returns true when the caller now
	 * OWNS the frame and must call end() in a finally; nested participants get false and just
	 * log into the existing frame. When FLOW is OFF this is a no-op returning false.
	 */
	public static boolean begin() {
		if (!LOG.isDebugEnabled() || ThreadContext.containsKey(REQ_ID_KEY)) {
			return false;
		}
		String reqId = String.format("%04x", ThreadLocalRandom.current().nextInt(0x10000));
		ThreadContext.put(REQ_ID_KEY, reqId);
		HOP.get()[0] = 0;
		return true;
	}

	/** Closes the frame. Call only when begin() returned true, from a finally block. */
	public static void end() {
		ThreadContext.remove(REQ_ID_KEY);
		HOP.remove();
	}

	/** One narrative line: {@code FLOW [reqId|hop] SimpleClassName → event}. */
	public static void log(Class<?> source, String event) {
		if (!LOG.isDebugEnabled()) {
			return;
		}
		String reqId = ThreadContext.get(REQ_ID_KEY);
		int hop = ++HOP.get()[0];
		LOG.debug("[{}|{}] {} → {}", reqId == null ? "----" : reqId, hop, source.getSimpleName(), event);
	}

}
