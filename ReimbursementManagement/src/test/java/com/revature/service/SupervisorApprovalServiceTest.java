package com.revature.service;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Hierarchy;
import com.revature.model.Request;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalStatus;
import com.revature.model.User;
import com.revature.repository.SupervisorApprovalRepositoryImpl;

public class SupervisorApprovalServiceTest {

	@InjectMocks private static SupervisorApprovalService sas;
	@Mock private static SupervisorApprovalRepositoryImpl sari;

	@BeforeClass
	public static void setupBeforeClass() {
		sas = new SupervisorApprovalService();
	}

	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testFindAll() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser2 = new User(2, "Stone Ocean", "Not yet aired", "Jolene", "Kujo", "so@email.com", new Role(2, "Jailed in Florida"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 100, "Tampa Beach", new CityStatePostal(1, "Orlando", "Florida")), "Stopping Pucci", mockUser1, new RequestStatus(1, "Oraoraora"));
		Request mockRequest2 = new Request(2, 1000.00, Date.valueOf("2000-01-02"), new EventLocation(2, 100, "Tampa Beach", new CityStatePostal(1, "Orlando", "Florida")), "Stopping Pucci", mockUser2, new RequestStatus(1, "Oraoraora"));
		Request mockRequest3 = new Request(3, 10000.00, Date.valueOf("2021-02-14"), new EventLocation(3, 100, "Sahara Desert", new CityStatePostal(3, "Oasis", "Egypt")), "Traveling to Egypt", mockUser2, new RequestStatus(1, "Oraoraora"));
		Hierarchy mockRelationship1 = new Hierarchy(1, mockUser3, mockUser1);
		Hierarchy mockRelationship2 = new Hierarchy(2, mockUser3, mockUser2);
		SupervisorApprovalStatus mockStatus1 = new SupervisorApprovalStatus(1, "Exploding");
		Date eventDate = Date.valueOf("2000-01-01");
		SupervisorApproval mockApproval1 = new SupervisorApproval(1, eventDate, mockRequest1, mockRelationship1, mockStatus1, false);
		SupervisorApproval mockApproval2 = new SupervisorApproval(2, eventDate, mockRequest2, mockRelationship2, mockStatus1, false);
		SupervisorApproval mockApproval3 = new SupervisorApproval(3, eventDate, mockRequest3, mockRelationship2, mockStatus1, false);
		Mockito.when(sari.findAll()).thenReturn(Arrays.asList(mockApproval1, mockApproval2, mockApproval3));
		List<SupervisorApproval> retrievedApprovals = sas.findAll();
		Assert.assertEquals("Stopping Pucci", retrievedApprovals.get(0).getRequest().getRequestedEvent());
	}

	@Test
	public void testFindById() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 100, "Tampa Beach", new CityStatePostal(1, "Orlando", "Florida")), "Stopping Pucci", mockUser1, new RequestStatus(1, "Oraoraora"));
		Hierarchy mockRelationship1 = new Hierarchy(1, mockUser3, mockUser1);
		SupervisorApprovalStatus mockStatus1 = new SupervisorApprovalStatus(1, "Exploding");
		Date eventDate = Date.valueOf("2000-01-01");
		SupervisorApproval mockApproval1 = new SupervisorApproval(1, eventDate, mockRequest1, mockRelationship1, mockStatus1, false);
		Mockito.when(sari.findById(1)).thenReturn(mockApproval1);
		SupervisorApproval retrievedApproval = sas.findById(1);
		Assert.assertEquals("mudamudamuda", retrievedApproval.getHierarchy().getSupervisorUser().getPassword());
	}

	@Test
	public void testFindByRequestAndRequester() {
		Request request = new Request();
		User requester = new User(1, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		SupervisorApproval approval = new SupervisorApproval(1, Date.valueOf("2000-01-01"), request, null, null, false);
		Mockito.when(sari.findByRequestAndRequester(request, requester)).thenReturn(Arrays.asList(approval));

		List<SupervisorApproval> result = sas.findByRequestAndRequester(request, requester);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(approval, result.get(0));
	}

	@Test
	public void testFindByRequestRequesterManager() {
		Request request = new Request();
		User requester = new User(1, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		User manager = new User(2, "mgr", "pw", "M", "M", "m@email.com", new Role(1, "Supervisor"));
		SupervisorApproval approval = new SupervisorApproval(1, Date.valueOf("2000-01-01"), request, null, null, false);
		Mockito.when(sari.findByRequestRequesterManager(request, requester, manager)).thenReturn(approval);

		SupervisorApproval result = sas.findByRequestRequesterManager(request, requester, manager);
		Assert.assertSame(approval, result);
	}

	@Test
	public void testFindPendingRequestsForManager() {
		User manager = new User(1, "mgr", "pw", "M", "M", "m@email.com", new Role(1, "Supervisor"));
		User other = new User(2, "other", "pw", "O", "O", "o@email.com", new Role(1, "Supervisor"));
		User emp = new User(3, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		SupervisorApprovalStatus pending = new SupervisorApprovalStatus(2, "Pending");
		SupervisorApprovalStatus resolved = new SupervisorApprovalStatus(1, "Resolved");
		Date d = Date.valueOf("2000-01-01");

		// KEEP: status 2 (pending) AND supervisor == manager
		SupervisorApproval included = new SupervisorApproval(1, d, new Request(), new Hierarchy(1, manager, emp), pending, false);
		// DROP: right manager, wrong status
		SupervisorApproval wrongStatus = new SupervisorApproval(2, d, new Request(), new Hierarchy(2, manager, emp), resolved, false);
		// DROP: right status, wrong manager
		SupervisorApproval wrongManager = new SupervisorApproval(3, d, new Request(), new Hierarchy(3, other, emp), pending, false);
		Mockito.when(sari.findAll()).thenReturn(Arrays.asList(included, wrongStatus, wrongManager));

		List<SupervisorApproval> result = sas.findPendingRequestsForManager(manager);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(included, result.get(0));
	}

	@Test
	public void testFindWhoResolvedAllRequests() {
		User u = new User(1, "u", "pw", "U", "U", "u@email.com", new Role(1, "Supervisor"));
		Hierarchy h = new Hierarchy(1, u, u);
		SupervisorApprovalStatus resolved = new SupervisorApprovalStatus(1, "Resolved");
		SupervisorApprovalStatus pending = new SupervisorApprovalStatus(2, "Pending");
		Date d = Date.valueOf("2000-01-01");

		SupervisorApproval resolvedApproval = new SupervisorApproval(1, d, new Request(), h, resolved, true);
		SupervisorApproval pendingApproval = new SupervisorApproval(2, d, new Request(), h, pending, false);
		Mockito.when(sari.findAll()).thenReturn(Arrays.asList(resolvedApproval, pendingApproval));

		List<SupervisorApproval> result = sas.findWhoResolvedAllRequests();
		Assert.assertEquals(1, result.size());
		Assert.assertSame(resolvedApproval, result.get(0));
	}

	@Test
	public void testFindRequestsMadeByEmployee() {
		User manager = new User(1, "mgr", "pw", "M", "M", "m@email.com", new Role(1, "Supervisor"));
		User employee = new User(2, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		User otherEmployee = new User(3, "other", "pw", "O", "O", "o@email.com", new Role(2, "Employee"));
		SupervisorApprovalStatus status = new SupervisorApprovalStatus(2, "Pending");
		Date d = Date.valueOf("2000-01-01");

		Request targetRequest = new Request();
		// MATCH: hierarchy supervisor == manager AND employee == employee
		SupervisorApproval match = new SupervisorApproval(1, d, targetRequest, new Hierarchy(1, manager, employee), status, false);
		// NO MATCH: different employee
		SupervisorApproval noMatch = new SupervisorApproval(2, d, new Request(), new Hierarchy(2, manager, otherEmployee), status, false);
		Mockito.when(sari.findAll()).thenReturn(Arrays.asList(match, noMatch));

		List<Request> result = sas.findRequestsMadeByEmployee(manager, employee);
		Assert.assertEquals(1, result.size());
		Assert.assertSame(targetRequest, result.get(0));
	}

	@Test
	public void testAddApproval() {
		SupervisorApproval approval = new SupervisorApproval(1, Date.valueOf("2000-01-01"), new Request(), null, null, false);
		sas.addApproval(approval);
		Mockito.verify(sari).addApproval(approval);
	}

	@Test
	public void testUpdateApproval() {
		SupervisorApproval approval = new SupervisorApproval(1, Date.valueOf("2000-01-01"), new Request(), null, null, false);
		sas.updateApproval(approval);
		Mockito.verify(sari).updateApproval(approval);
	}

	@Test
	public void testDeleteApproval() {
		SupervisorApproval approval = new SupervisorApproval(1, Date.valueOf("2000-01-01"), new Request(), null, null, false);
		sas.deleteApproval(approval);
		// deleteApproval has an empty body: it must NOT touch the repository (documents the no-op stub)
		Mockito.verifyNoInteractions(sari);
	}

}
