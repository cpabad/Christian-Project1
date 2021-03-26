package com.revature.util;

import java.io.File;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class ClientBuilder {
	
//	private final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
//			.withRegion(Regions.US_EAST_2)
//			.withCredentials(new EnvironmentVariableCredentialsProvider());
//	
//	public AmazonDynamoDB createClient() {
//		return builder.build();
//	}
	
	public AWSCredentials basicCredentials() {
//		AWSCredentialsProvider provider1 = new EnvironmentVariableCredentialsProvider();
//		System.out.println(provider1.getCredentials());
//		AWSCredentialsProvider provider2 = new SystemPropertiesCredentialsProvider();
//		System.out.println(provider2.getCredentials());
		AWSCredentials credentials = new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"));
		return credentials;
		
	}
	
	public void viewMyCredentials() {
		
	}
	
	public void uploadObject() {
		String file_path = "F:\\1-25-2021-msa\\project\\reimbursement_management\\images\\receipt.jpg";
		File file = new File(file_path);
		String bucket_name = "p1jan25bucket";
		System.out.format("Uploading %s to S3 bucket %s...\n",  file_path, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_2)
				.build();
		System.out.println(s3.getS3AccountOwner());
		try {
			s3.putObject(bucket_name, "receipt.jpg", file);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}

}
