package com.revature.service;

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

import com.revature.model.AmazonS3Object;
import com.revature.model.Request;
import com.revature.repository.AmazonS3ObjectRepositoryImpl;

public class AmazonS3ObjectServiceTest {

	@InjectMocks private static AmazonS3ObjectService s3Service;
	@Mock private static AmazonS3ObjectRepositoryImpl s3Repository;

	@BeforeClass
	public static void setupBeforeClass() {
		s3Service = new AmazonS3ObjectService();
	}

	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testFindById() {
		AmazonS3Object stub = new AmazonS3Object(1, "receipt.jpg", null);
		Mockito.when(s3Repository.findById(1)).thenReturn(stub);
		Assert.assertEquals("receipt.jpg", s3Service.findById(1).getfileName());
	}

	@Test
	public void testFindByURL() {
		AmazonS3Object stub = new AmazonS3Object(2, "diagram.png", null);
		Mockito.when(s3Repository.findByURL("diagram.png")).thenReturn(stub);
		Assert.assertEquals(2, s3Service.findByURL("diagram.png").getImageId());
	}

	@Test
	public void testFindByRequest() {
		Request request = new Request();
		AmazonS3Object first = new AmazonS3Object(3, "first.jpg", request);
		AmazonS3Object second = new AmazonS3Object(4, "second.jpg", request);
		Mockito.when(s3Repository.findByRequest(request)).thenReturn(Arrays.asList(first, second));

		List<AmazonS3Object> retrieved = s3Service.findByRequest(request);
		Assert.assertEquals(2, retrieved.size());
		Assert.assertEquals("first.jpg", retrieved.get(0).getfileName());
		Assert.assertEquals("second.jpg", retrieved.get(1).getfileName());
	}

	@Test
	public void testAddObject() {
		AmazonS3Object toAdd = new AmazonS3Object(5, "upload.jpg", new Request());
		s3Service.addObject(toAdd);
		Mockito.verify(s3Repository).addObject(toAdd);
	}

	@Test
	public void testDeleteObject() {
		AmazonS3Object toDelete = new AmazonS3Object(6, "remove.jpg", new Request());
		s3Service.deleteObject(toDelete);
		Mockito.verify(s3Repository).deleteObject(toDelete);
	}
}
