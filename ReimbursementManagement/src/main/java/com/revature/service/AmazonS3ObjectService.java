package com.revature.service;

import java.util.List;

import com.revature.model.AmazonS3Object;
import com.revature.model.Request;
import com.revature.repository.AmazonS3ObjectRepository;
import com.revature.repository.AmazonS3ObjectRepositoryImpl;
import com.revature.util.FlowTrace;

public class AmazonS3ObjectService {
	
	private AmazonS3ObjectRepository s3Repository;
	
	public AmazonS3ObjectService() {
		s3Repository = new AmazonS3ObjectRepositoryImpl();
	}
	
	public AmazonS3Object findById(int id) {
		FlowTrace.log(AmazonS3ObjectService.class, "findById: service operation begins");
		return this.s3Repository.findById(id);
	}
	
	public AmazonS3Object findByURL(String url) {
		FlowTrace.log(AmazonS3ObjectService.class, "findByURL: service operation begins");
		return this.s3Repository.findByURL(url);
	}
	
	public List<AmazonS3Object> findByRequest(Request request) {
		FlowTrace.log(AmazonS3ObjectService.class, "findByRequest: service operation begins");
		return this.s3Repository.findByRequest(request);
	}
	
	public void addObject(AmazonS3Object object) {
		FlowTrace.log(AmazonS3ObjectService.class, "addObject: service operation begins");
		this.s3Repository.addObject(object);
	}
	
	public void deleteObject(AmazonS3Object object) {
		FlowTrace.log(AmazonS3ObjectService.class, "deleteObject: service operation begins");
		this.s3Repository.deleteObject(object);
	}

}
