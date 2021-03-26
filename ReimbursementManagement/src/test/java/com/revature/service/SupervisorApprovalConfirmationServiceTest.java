package com.revature.service;

import java.sql.Date;

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
import com.revature.model.SupervisorApprovalConfirmation;
import com.revature.model.SupervisorApprovalStatus;
import com.revature.model.User;
import com.revature.repository.SupervisorApprovalConfirmationRepositoryImpl;

public class SupervisorApprovalConfirmationServiceTest {
	
	@InjectMocks private static SupervisorApprovalConfirmationService sacs;
	@Mock private static SupervisorApprovalConfirmationRepositoryImpl sacri;
	
	@BeforeClass
	public static void setupBeforeClass() {
		sacs = new SupervisorApprovalConfirmationService();
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
		SupervisorApprovalConfirmation mockConfirmation1 = new SupervisorApprovalConfirmation(1, eventDate, mockApproval1);
		Mockito.when(sacri.findById(1)).thenReturn(mockConfirmation1);
		SupervisorApprovalConfirmation retrievedConfirmation = sacs.findById(1);
		Assert.assertEquals("mudamudamuda", retrievedConfirmation.getApproval().getHierarchy().getSupervisorUser().getPassword());
	}
	
	@Test
	public void testFindByDateAndApproval() {
		User mockUser1 = new User(1, "Star Platinum", "Stand Proud", "Jotaro", "Kujo", "sp@email.com", new Role(1, "Being awesome"));
		User mockUser3 = new User(3, "The World", "mudamudamuda", "Dio", "Brando", "tw@email.com", new Role(3, "Being Dio"));
		Request mockRequest1 = new Request(1, 1000.00, Date.valueOf("2000-01-01"), new EventLocation(1, 100, "Tampa Beach", new CityStatePostal(1, "Orlando", "Florida")), "Stopping Pucci", mockUser1, new RequestStatus(1, "Oraoraora"));
		Hierarchy mockRelationship1 = new Hierarchy(1, mockUser3, mockUser1);
		SupervisorApprovalStatus mockStatus1 = new SupervisorApprovalStatus(1, "Exploding");
		Date eventDate = Date.valueOf("2000-01-01");
		SupervisorApproval mockApproval1 = new SupervisorApproval(1, eventDate, mockRequest1, mockRelationship1, mockStatus1, false);
		SupervisorApprovalConfirmation mockConfirmation1 = new SupervisorApprovalConfirmation(1, eventDate, mockApproval1);
		Mockito.when(sacri.findByDateAndApproval("2000-01-01", mockApproval1)).thenReturn(mockConfirmation1);
		SupervisorApprovalConfirmation retrievedConfirmation = sacs.findByDateAndApproval("2000-01-01", mockApproval1);
		Assert.assertEquals("mudamudamuda", retrievedConfirmation.getApproval().getHierarchy().getSupervisorUser().getPassword());
	}

}
