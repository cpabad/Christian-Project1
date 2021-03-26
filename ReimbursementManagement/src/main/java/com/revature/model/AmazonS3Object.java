package com.revature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "request_image", schema = "\"ExpenseReimbursementManagementSystem\"")
public class AmazonS3Object {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int imageId;
	
	@Column
	private String fileName;
	
	@ManyToOne
	@JoinColumn(name = "requestId")
	private Request request;

	public int getImageId() {
		return imageId;
	}

	public String getfileName() {
		return fileName;
	}

	public Request getRequest() {
		return request;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public void setfileName(String fileName) {
		this.fileName = fileName;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + imageId;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmazonS3Object other = (AmazonS3Object) obj;
		if (imageId != other.imageId)
			return false;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AmazonS3Object [imageId=" + imageId + ", fileName=" + fileName + ", request=" + request + "]";
	}

	public AmazonS3Object(int imageId, String fileName, Request request) {
		super();
		this.imageId = imageId;
		this.fileName = fileName;
		this.request = request;
	}

	public AmazonS3Object() {
		super();
	}

	
	

}
