package com.revature.controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HealthServletTest {

	// the servlet under test with the database verdict pinned, so both branches are
	// testable without standing a database up or tearing one down mid-suite
	private static HealthServlet servletReporting(final boolean dbReachable) {
		return new HealthServlet() {
			private static final long serialVersionUID = 1L;
			protected boolean databaseIsReachable() {
				return dbReachable;
			}
		};
	}

	@Test
	public void testHealthUpWhenDatabaseReachable() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter body = new StringWriter();
		Mockito.when(response.getWriter()).thenReturn(new PrintWriter(body));

		servletReporting(true).doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
		Mockito.verify(response).setContentType("application/json");
		Assert.assertEquals("{\"status\":\"UP\"}", body.toString());
	}

	@Test
	public void testHealthDownWhenDatabaseUnreachable() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter body = new StringWriter();
		Mockito.when(response.getWriter()).thenReturn(new PrintWriter(body));

		servletReporting(false).doGet(request, response);

		Mockito.verify(response).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		Mockito.verify(response).setContentType("application/json");
		Assert.assertEquals("{\"status\":\"DOWN\"}", body.toString());
	}

	@Test
	public void testDatabaseProbeAgainstRealDatabase() {
		// integration flavor, like the repository tests: the suite already requires the
		// seeded database (dburl/dbuser/dbpassword env vars), so the real probe must pass
		Assert.assertTrue(new HealthServlet().databaseIsReachable());
	}

}
