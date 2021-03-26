package com.revature.repository;

import java.util.List;

import com.revature.model.AmazonS3Object;
import com.revature.model.Request;

public interface AmazonS3ObjectRepository {
	
	AmazonS3Object findById(int id);
	AmazonS3Object findByURL(String url);
	List<AmazonS3Object> findByRequest(Request request);
	void addObject (AmazonS3Object object);
	void deleteObject(AmazonS3Object object);

}
