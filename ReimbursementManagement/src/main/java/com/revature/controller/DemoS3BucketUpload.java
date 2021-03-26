package com.revature.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Servlet implementation class DemoS3BucketUpload
 */
//@WebServlet("/upload-file")
//@MultipartConfig(location = "C:\\Users\\ca132\\Desktop")
public class DemoS3BucketUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DemoS3BucketUpload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Getting the file input stream uploading locally
		Part filePart = request.getPart("myFile1");
		filePart.write("request_part");
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		String file_path = "C:\\Users\\ca132\\Desktop" + fileName;
		File file = new File(file_path);
		
		
		
		//Uploading to the bucket
		String bucket_name = "p1jan25bucket";
		System.out.format("Uploading %s to S3 bucket %s...\n",  fileName, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_2)
				.build();
		try {
			s3.putObject(bucket_name, "Wow such a great file name", file);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (AmazonClientException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Done!");
		RequestDispatcher dispatcher = request.getRequestDispatcher("app/upload-file");
		dispatcher.forward(request, response);		
	}

}
