package com.revature.service;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.revature.model.CityStatePostal;
import com.revature.model.EventLocation;
import com.revature.model.Hierarchy;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.Request;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalStatus;
import com.revature.model.User;
import com.revature.repository.HierarchyRepositoryImpl;
import com.revature.repository.ReimbursementRepositoryImpl;
import com.revature.repository.ReimbursementStatusRepositoryImpl;
import com.revature.repository.RequestRepositoryImpl;
import com.revature.repository.SupervisorApprovalRepositoryImpl;
import com.revature.repository.SupervisorApprovalStatusRepositoryImpl;

public class RequestServiceTest {

	@InjectMocks private static RequestService requestService;
	@Mock private static RequestRepositoryImpl requestRepository;
	@Mock private static HierarchyRepositoryImpl hierarchyRepository;
	@Mock private static SupervisorApprovalRepositoryImpl supervisorApprovalRepository;
	@Mock private static SupervisorApprovalStatusRepositoryImpl supervisorApprovalStatusRepository;
	@Mock private static ReimbursementRepositoryImpl reimbursementRepository;
	@Mock private static ReimbursementStatusRepositoryImpl reimbursementStatusRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		requestService = new RequestService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		Request mockRequest = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Mockito.when(requestRepository.findById(1)).thenReturn(mockRequest);
		Request retrievedRequest = requestService.findById(1);
		Assert.assertEquals("Rumbling", retrievedRequest.getRequestedEvent());
	}
	
	@Test
	public void testFindByDateLocationUser() {
		Request mockRequest = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Mockito.when(requestRepository.findByDateLocationRequester("2000-01-01", new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")))).thenReturn(mockRequest);
		Request retrievedRequest = requestService.findByDateLocationRequester("2000-01-01", new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")));
		Assert.assertEquals("Rumbling", retrievedRequest.getRequestedEvent());
	}
	
	@Test
	public void testFindAll() {
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Request mockRequest2 = new Request(2, 1000.00, Date.valueOf("2000-01-02"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Walking", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Request mockRequest3 = new Request(3, 10000.00, Date.valueOf("2021-02-14"), new EventLocation(1, 1, "E-Rantel Markets", new CityStatePostal(2, "E-Rantel", "Re-Estize Kingdom")), "Being Momon", new User(2, "Momonga", "ainzooalgown", "Suzuki", "Satoru", "ss@email.com", new Role(2, "Guild Master")), new RequestStatus(2, "Approved because all hail Momonga"));
		Mockito.when(requestRepository.findAll()).thenReturn(Arrays.asList(mockRequest1, mockRequest2, mockRequest3));
		List<Request> retrievedRequests = requestService.findAll();
		Assert.assertEquals("Rumbling", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Walking", retrievedRequests.get(1).getRequestedEvent());
		Assert.assertEquals("Being Momon", retrievedRequests.get(2).getRequestedEvent());
	}
	
	@Test
	public void testFindByRequester() {
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Request mockRequest2 = new Request(2, 1000.00, Date.valueOf("2000-01-02"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Walking", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Mockito.when(requestRepository.findByRequester(new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")))).thenReturn(Arrays.asList(mockRequest1, mockRequest2));
		List<Request> retrievedRequests = requestService.findByRequester(new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")));
		Assert.assertEquals("Rumbling", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Walking", retrievedRequests.get(1).getRequestedEvent());
	}
	
	@Test
	public void testFindByRequesterAndPendingStatus() {
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Rumbling", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Request mockRequest2 = new Request(2, 1000.00, Date.valueOf("2000-01-02"), new EventLocation(1, 0, "Fritz Rd", new CityStatePostal(1, "Paradiso", "Eldia")), "Walking", new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")), new RequestStatus(1, "Waiting for the Rumbling to end"));
		Mockito.when(requestRepository.findByRequesterAndPendingStatus(new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")))).thenReturn(Arrays.asList(mockRequest1, mockRequest2));
		List<Request> retrievedRequests = requestService.findByRequesterAndPendingStatus(new User(1, "eldianDevil", "eldiaftw", "Eren", "Jaegar", "ej@email.com", new Role(1, "Rumbling")));
		Assert.assertEquals("Rumbling", retrievedRequests.get(0).getRequestedEvent());
		Assert.assertEquals("Walking", retrievedRequests.get(1).getRequestedEvent());
	}

	@Test
	public void testFindByRequesterAndResolvedStatus() {
		User requester = new User(1, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		Request resolved = new Request(1, 100.00, Date.valueOf("2000-01-01"), new EventLocation(1, 0, "Main St", new CityStatePostal(1, "City", "State")), "Resolved Event", requester, new RequestStatus(1, "Resolved"));
		Mockito.when(requestRepository.findByRequesterAndResolvedStatus(requester)).thenReturn(Arrays.asList(resolved));
		List<Request> result = requestService.findByRequesterAndResolvedStatus(requester);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("Resolved Event", result.get(0).getRequestedEvent());
	}

	@Test
	public void testMakeNewRequest() {
		Request request = new Request();
		requestService.makeNewRequest(request);
		Mockito.verify(requestRepository).makeNewRequest(request);
	}

	@Test
	public void testSubmitRequestCreatesReimbursementAtChainTop() {
		// The requester's supervisor has nobody above them: the fan-out creates
		// both the pending approval and the pending reimbursement record.
		User emp = new User(1, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		User mgr = new User(5, "mgr", "pw", "M", "M", "m@email.com", new Role(1, "Supervisor"));
		EventLocation loc = new EventLocation(1, 100, "Main St", new CityStatePostal(1, "City", "State"));
		Request newRequest = new Request(100, 250.0, Date.valueOf("2026-01-15"), loc, "Conference", emp, new RequestStatus(2, "Pending"));
		Request persisted = new Request(9, 250.0, Date.valueOf("2026-01-15"), loc, "Conference", emp, new RequestStatus(2, "Pending"));
		Hierarchy mgrOverEmp = new Hierarchy(1, mgr, emp);
		SupervisorApprovalStatus approvalPending = new SupervisorApprovalStatus(2, "Pending");
		ReimbursementStatus reimbursementPending = new ReimbursementStatus(2, "Pending");

		Mockito.when(requestRepository.findByDateLocationRequester("2026-01-15", loc, emp)).thenReturn(persisted);
		Mockito.when(hierarchyRepository.findByEmployee(emp)).thenReturn(Arrays.asList(mgrOverEmp));
		Mockito.when(hierarchyRepository.findByEmployee(mgr)).thenReturn(Collections.<Hierarchy>emptyList());
		Mockito.when(supervisorApprovalStatusRepository.findById(2)).thenReturn(approvalPending);
		Mockito.when(reimbursementStatusRepository.findById(2)).thenReturn(reimbursementPending);

		Request result = requestService.submitRequest(newRequest);

		Assert.assertSame(persisted, result);
		Mockito.verify(requestRepository).makeNewRequest(newRequest);
		ArgumentCaptor<SupervisorApproval> approvalCaptor = ArgumentCaptor.forClass(SupervisorApproval.class);
		Mockito.verify(supervisorApprovalRepository).addApproval(approvalCaptor.capture());
		Assert.assertSame(persisted, approvalCaptor.getValue().getRequest());
		Assert.assertSame(mgrOverEmp, approvalCaptor.getValue().getHierarchy());
		Assert.assertSame(approvalPending, approvalCaptor.getValue().getSupervisorApprovalStatus());
		ArgumentCaptor<Reimbursement> reimbursementCaptor = ArgumentCaptor.forClass(Reimbursement.class);
		Mockito.verify(reimbursementRepository).addReimbursement(reimbursementCaptor.capture());
		Assert.assertEquals(250.0, reimbursementCaptor.getValue().getAmount(), 0.001);
		Assert.assertSame(reimbursementPending, reimbursementCaptor.getValue().getReimbursementStatus());
	}

	@Test
	public void testSubmitRequestNoReimbursementBelowChainTop() {
		// The requester's supervisor reports upward themselves: the fan-out
		// creates the approval but no reimbursement record yet.
		User emp = new User(1, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		User mgr = new User(5, "mgr", "pw", "M", "M", "m@email.com", new Role(1, "Supervisor"));
		User boss = new User(7, "boss", "pw", "B", "B", "b@email.com", new Role(1, "Supervisor"));
		EventLocation loc = new EventLocation(1, 100, "Main St", new CityStatePostal(1, "City", "State"));
		Request newRequest = new Request(100, 250.0, Date.valueOf("2026-01-15"), loc, "Conference", emp, new RequestStatus(2, "Pending"));
		Request persisted = new Request(9, 250.0, Date.valueOf("2026-01-15"), loc, "Conference", emp, new RequestStatus(2, "Pending"));
		Hierarchy mgrOverEmp = new Hierarchy(1, mgr, emp);
		Hierarchy bossOverMgr = new Hierarchy(2, boss, mgr);

		Mockito.when(requestRepository.findByDateLocationRequester("2026-01-15", loc, emp)).thenReturn(persisted);
		Mockito.when(hierarchyRepository.findByEmployee(emp)).thenReturn(Arrays.asList(mgrOverEmp));
		Mockito.when(hierarchyRepository.findByEmployee(mgr)).thenReturn(Arrays.asList(bossOverMgr));
		Mockito.when(supervisorApprovalStatusRepository.findById(2)).thenReturn(new SupervisorApprovalStatus(2, "Pending"));

		Request result = requestService.submitRequest(newRequest);

		Assert.assertSame(persisted, result);
		Mockito.verify(supervisorApprovalRepository).addApproval(Mockito.any(SupervisorApproval.class));
		Mockito.verifyNoInteractions(reimbursementRepository);
		Mockito.verifyNoInteractions(reimbursementStatusRepository);
	}

	@Test
	public void testSubmitRequestWithoutSupervisors() {
		// No hierarchy rows for the requester: the request persists but there is
		// no approval chain to fan out.
		User emp = new User(1, "emp", "pw", "E", "E", "e@email.com", new Role(2, "Employee"));
		EventLocation loc = new EventLocation(1, 100, "Main St", new CityStatePostal(1, "City", "State"));
		Request newRequest = new Request(100, 250.0, Date.valueOf("2026-01-15"), loc, "Conference", emp, new RequestStatus(2, "Pending"));
		Request persisted = new Request(9, 250.0, Date.valueOf("2026-01-15"), loc, "Conference", emp, new RequestStatus(2, "Pending"));

		Mockito.when(requestRepository.findByDateLocationRequester("2026-01-15", loc, emp)).thenReturn(persisted);
		Mockito.when(hierarchyRepository.findByEmployee(emp)).thenReturn(Collections.<Hierarchy>emptyList());

		Request result = requestService.submitRequest(newRequest);

		Assert.assertSame(persisted, result);
		Mockito.verify(requestRepository).makeNewRequest(newRequest);
		Mockito.verifyNoInteractions(supervisorApprovalRepository);
		Mockito.verifyNoInteractions(reimbursementRepository);
	}

	@Test
	public void testUpdateRequest() {
		Request request = new Request();
		requestService.updateRequest(request);
		Mockito.verify(requestRepository).updateRequest(request);
	}

	@Test
	public void testDeleteRequest() {
		Request request = new Request();
		requestService.deleteRequest(request);
		// deleteRequest has an empty body: it must NOT touch the repository (documents the no-op stub)
		Mockito.verifyNoInteractions(requestRepository);
	}

}
