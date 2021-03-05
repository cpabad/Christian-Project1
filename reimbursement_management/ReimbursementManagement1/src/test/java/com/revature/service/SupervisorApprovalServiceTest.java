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
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 100, "Tampa Beach", new CityStatePostal(1, "Orlando", "Florida")), "Stopping Pucci", mockUser1, new RequestStatus(1, "Oraoraora"));
		Hierarchy mockRelationship1 = new Hierarchy(1, mockUser3, mockUser1);
		SupervisorApprovalStatus mockStatus1 = new SupervisorApprovalStatus(1, "Exploding");
		Date eventDate = Date.valueOf("2000-01-01");
		SupervisorApproval mockApproval1 = new SupervisorApproval(1, eventDate, mockRequest1, mockRelationship1, mockStatus1, false);
		Mockito.when(sari.findByRequestAndRequester(mockRequest1, mockUser1)).thenReturn(mockApproval1);
		SupervisorApproval retrievedApproval = sas.findByRequestAndRequester(mockRequest1, mockUser1);
		Assert.assertEquals("mudamudamuda", retrievedApproval.getHierarchy().getSupervisorUser().getPassword());

	}

}
