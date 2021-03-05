package com.revature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "request_image", schema = "\"ExpenseReimbursementManagementSystem\"")
public class AmazonS3Object {
	
	@Id
	@Column
	private int imageId;
	
	@Column
	private String url;
	
	@OneToOne
	@JoinColumn(name = "requestId")
	private Request request;

	public int getImageId() {
		return imageId;
	}

	public String getUrl() {
		return url;
	}

	public Request getRequest() {
		return request;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public void setUrl(String url) {
		this.url = url;
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
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AmazonS3Object [imageId=" + imageId + ", url=" + url + ", request=" + request + "]";
	}

	public AmazonS3Object(int imageId, String url, Request request) {
		super();
		this.imageId = imageId;
		this.url = url;
		this.request = request;
	}

	public AmazonS3Object() {
		super();
	}

	
	

}
