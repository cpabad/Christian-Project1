package com.revature.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Receives a receipt image (multipart/form-data) and stores it in the S3 bucket.
 * Parts are buffered in the container's temp directory (no fixed filesystem path);
 * AWS credentials come from the SDK's default provider chain (environment variables).
 */
@WebServlet("/upload-file")
@MultipartConfig
public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(UploadFile.class);

	private static final String BUCKET_NAME = "p1jan25bucket";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Part filePart = request.getPart("myFile");
		// @MultipartConfig has no location, so relative writes land in the container's temp directory
		filePart.write("request_part");
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		File tempDir = (File) getServletContext().getAttribute(ServletContext.TEMPDIR);
		File file = new File(tempDir, "request_part");
		LOG.debug("Uploading " + fileName + " to S3 bucket " + BUCKET_NAME);
		final AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_2)
				.build();
		try {
			s3.putObject(BUCKET_NAME, fileName, file);
		} catch (AmazonServiceException e) {
			LOG.error("S3 upload failed", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The file upload failed. Please try again later.");
			return;
		}
		LOG.debug("Upload of " + fileName + " complete");
		request.setAttribute("fileName", fileName);
		RequestDispatcher dispatcher = request.getRequestDispatcher("app/upload-file");
		dispatcher.forward(request, response);
	}

}
