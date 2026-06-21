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
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.Request;
import com.revature.model.RequestStatus;
import com.revature.model.Role;
import com.revature.model.SupervisorApproval;
import com.revature.model.SupervisorApprovalStatus;
import com.revature.model.User;
import com.revature.repository.ReimbursementRepositoryImpl;

public class ReimbursementServiceTest {

	@InjectMocks private static ReimbursementService reimbursementService;
	@Mock private static ReimbursementRepositoryImpl reimbursementRepository;

	@BeforeClass
	public static void setupBeforeClass() {
		reimbursementService = new ReimbursementService();
	}

	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
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
		Reimbursement mockReimbursement1 = new Reimbursement(1, 100000.99, eventDate, mockApproval1, new ReimbursementStatus(1, "Exploding"));
		Mockito.when(reimbursementRepository.findById(1)).thenReturn(mockReimbursement1);
		Reimbursement retrievedAward = reimbursementService.findById(1);
		Assert.assertEquals("mudamudamuda", retrievedAward.getFinalApproval().getHierarchy().getSupervisorUser().getPassword());
	}

	@Test
	public void testFindByRequest() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 100, "Tampa Beach", new CityStatePostal(1, "Orlando", "Florida")), "Stopping Pucci", mockUser1, new RequestStatus(1, "Oraoraora"));
		Hierarchy mockRelationship1 = new Hierarchy(1, mockUser3, mockUser1);
		SupervisorApprovalStatus mockStatus1 = new SupervisorApprovalStatus(1, "Exploding");
		Date eventDate = Date.valueOf("2000-01-01");
		SupervisorApproval mockApproval1 = new SupervisorApproval(1, eventDate, mockRequest1, mockRelationship1, mockStatus1, false);
		Reimbursement mockReimbursement1 = new Reimbursement(1, 100000.99, eventDate, mockApproval1, new ReimbursementStatus(1, "Exploding"));
		Mockito.when(reimbursementRepository.findByRequest(mockRequest1)).thenReturn(mockReimbursement1);
		Reimbursement retrievedAward = reimbursementService.findByRequest(mockRequest1);
		Assert.assertEquals("mudamudamuda", retrievedAward.getFinalApproval().getHierarchy().getSupervisorUser().getPassword());

	}

	@Test
	public void testFindByEventDateAndRequester() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 100, "Tampa Beach", new CityStatePostal(1, "Orlando", "Florida")), "Stopping Pucci", mockUser1, new RequestStatus(1, "Oraoraora"));
		Hierarchy mockRelationship1 = new Hierarchy(1, mockUser3, mockUser1);
		SupervisorApprovalStatus mockStatus1 = new SupervisorApprovalStatus(1, "Exploding");
		Date eventDate = Date.valueOf("2000-01-01");
		SupervisorApproval mockApproval1 = new SupervisorApproval(1, eventDate, mockRequest1, mockRelationship1, mockStatus1, false);
		Reimbursement mockReimbursement1 = new Reimbursement(1, 100000.99, eventDate, mockApproval1, new ReimbursementStatus(1, "Exploding"));
		Mockito.when(reimbursementRepository.findByEventDateAndRequester("2000-01-01", mockUser1)).thenReturn(mockReimbursement1);
		Reimbursement retrievedAward = reimbursementService.findByEventDateAndRequester("2000-01-01", mockUser1);
		Assert.assertEquals("mudamudamuda", retrievedAward.getFinalApproval().getHierarchy().getSupervisorUser().getPassword());

	}

	@Test
	public void testFindAll() {
		Reimbursement r1 = new Reimbursement(1, 100.00, Date.valueOf("2000-01-01"), null, new ReimbursementStatus(1, "Resolved"));
		Reimbursement r2 = new Reimbursement(2, 200.00, Date.valueOf("2000-01-02"), null, new ReimbursementStatus(2, "Pending"));
		Mockito.when(reimbursementRepository.findAll()).thenReturn(Arrays.asList(r1, r2));
		List<Reimbursement> result = reimbursementService.findAll();
		Assert.assertEquals(2, result.size());
		Assert.assertSame(r1, result.get(0));
	}

	@Test
	public void testAddReimbursement() {
		Reimbursement r = new Reimbursement(1, 100.00, Date.valueOf("2000-01-01"), null, new ReimbursementStatus(1, "Resolved"));
		reimbursementService.addReimbursement(r);
		Mockito.verify(reimbursementRepository).addReimbursement(r);
	}

	@Test
	public void testUpdateReimbursement() {
		Reimbursement r = new Reimbursement(1, 100.00, Date.valueOf("2000-01-01"), null, new ReimbursementStatus(1, "Resolved"));
		reimbursementService.updateReimbursement(r);
		Mockito.verify(reimbursementRepository).updateReimbursement(r);
	}

	@Test
	public void testDeleteReimbursement() {
		Reimbursement r = new Reimbursement(1, 100.00, Date.valueOf("2000-01-01"), null, new ReimbursementStatus(1, "Resolved"));
		reimbursementService.deleteReimbursement(r);
		// deleteReimbursement has an empty body: it must NOT touch the repository (documents the no-op stub)
		Mockito.verifyNoInteractions(reimbursementRepository);
	}

}
