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

import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Servlet implementation class UploadFile
 */
@WebServlet("/upload-file")
@MultipartConfig(location = "F:\\1-25-2021-msa\\project\\reimbursement_management\\images\\")
public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFile() {
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
////		FileItemFactory factory = new DiskFileItemFactory();
////		ServletFileUpload upload  = new ServletFileUpload(factory);
////		Iterator<FileItem> iterator = null;
////		InputStream fileInputStream = null;
////		String fileName = null;
////		try {
////			iterator = upload.
////		}
//		
////		String description = request.getParameter("description");
//		Collection<Part> filePart = request.getParts();
//		Path dirPath = Paths.get("F:\\1-25-2021-msa\\project\\reimbursement_management\\images\\");
//		Files.createTempDirectory(dirPath, "s3upload-");
//		List<File> files = new ArrayList<>();
//		int i = 0;
//		for(Part p : filePart) {
//			i++;
//			String fLocationInDir = "request_part" + i;
//			p.write(fLocationInDir);
////			String fileName = Paths.get(p.getSubmittedFileName()).getFileName().toString();
//			String file_path = "F:\\1-25-2021-msa\\project\\reimbursement_management\\images\\RequestParts\\" + fLocationInDir;
//			File file = new File(file_path);
//			files.add(file);
//		}
////		filePart.write("request_part");
////		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
//////		Path file_path = Paths.get(filePart.get)
////		String file_path = "F:\\1-25-2021-msa\\project\\reimbursement_management\\images\\request_part";
//////		CopyOption[] options = new CopyOption[] { 
//////				StandardCopyOption.ATOMIC_MOVE,
//////				StandardCopyOption.REPLACE_EXISTING
//////				};
//////		try (InputStream fileContent = filePart.getInputStream()) {
//////			Files.copy(fileContent, file_path, options);
//////		}
////		
//////		System.out.println(new File(".").getAbsolutePath());
////		
////		
////		File file = new File(file_path);
////		System.out.println(file_path);
//		String bucket_name = "p1jan25bucket";
//		System.out.format("Uploading %s to S3 bucket %s...\n",  dirPath, bucket_name);
//		final AmazonS3 s3 = AmazonS3ClientBuilder
//				.standard()
//				.withRegion(Regions.US_EAST_2)
//				.build();
//		final TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
//		System.out.println(s3.getS3AccountOwner());
//		try {
////			s3.putObject(bucket_name, (String) request.getSession(false).getAttribute("username"), files);
//			MultipleFileUpload xfer = xfer_mgr.uploadFileList(bucket_name, (String) request.getSession(false).getAttribute("username"), new File("."), files);
//			xfer.waitForCompletion();
//		} catch (AmazonServiceException e) {
//			System.err.println(e.getErrorMessage());
//			System.exit(1);
//		} catch (AmazonClientException e) {
//			e.printStackTrace();
//			System.exit(1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			System.exit(1);
//		} finally {
////			s3.shutdown();
//			xfer_mgr.shutdownNow();
//		}
//		System.out.println("Done!");
//		RequestDispatcher dispatcher = request.getRequestDispatcher("app/upload-file");
//		dispatcher.forward(request, response);
		
		
		
		FileItemFactory factory = new DiskFileItemFactory();
//		ServletFileUpload upload  = new ServletFileUpload(factory);
//		Iterator<FileItem> iterator = null;
//		InputStream fileInputStream = null;
//		String fileName = null;
//		try {
//			iterator = upload.
//		}
		
//		String description = request.getParameter("description");
		Part filePart = request.getPart("myFile");
		filePart.write("request_part");
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
//		Path file_path = Paths.get(filePart.get)
		String file_path = "F:\\1-25-2021-msa\\project\\reimbursement_management\\images\\request_part";
//		CopyOption[] options = new CopyOption[] { 
//				StandardCopyOption.ATOMIC_MOVE,
//				StandardCopyOption.REPLACE_EXISTING
//				};
//		try (InputStream fileContent = filePart.getInputStream()) {
//			Files.copy(fileContent, file_path, options);
//		}
		
//		System.out.println(new File(".").getAbsolutePath());
		
		
		File file = new File(file_path);
		String bucket_name = "p1jan25bucket";
		System.out.format("Uploading %s to S3 bucket %s...\n",  file_path, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_2)
				.build();
		System.out.println(s3.getS3AccountOwner());
		try {
			s3.putObject(bucket_name, fileName, file);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
		System.out.println("Done!");
		request.getSession(false).setAttribute("fileName", fileName);
		RequestDispatcher dispatcher = request.getRequestDispatcher("app/upload-file");
		dispatcher.forward(request, response);

		
	}

}
