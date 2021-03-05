package com.revature.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This is a model for confirmation that a request was created.
 * @author ca132
 *
 */
@Entity
@Table(name = "request_confirmation", schema = "\"ExpenseReimbursementManagementSystem\"")
public class RequestConfirmation {
	
	/**
	 * The confirmationId field is a unique numeric identifier of a request's confirmation
	 */
	@Column
	@Id
	private int confirmationId;
	/**
	 * The confirmationDate field is the date of the request's confirmation
	 */
	@Column
	private Date confirmationDate;
	/**
	 * The request field is the request information associated with the request confirmation.
	 */
	@ManyToOne
	@JoinColumn(name = "requestId")
	private Request request;
	public int getConfirmationId() {
		return confirmationId;
	}
	public void setConfirmationId(int confirmationId) {
		this.confirmationId = confirmationId;
	}
	public Date getConfirmationDate() {
		return confirmationDate;
	}
	public void setConfirmationDate(Date confirmationDate) {
		this.confirmationDate = confirmationDate;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((confirmationDate == null) ? 0 : confirmationDate.hashCode());
		result = prime * result + confirmationId;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
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
		RequestConfirmation other = (RequestConfirmation) obj;
		if (confirmationDate == null) {
			if (other.confirmationDate != null)
				return false;
		} else if (!confirmationDate.equals(other.confirmationDate))
			return false;
		if (confirmationId != other.confirmationId)
			return false;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RequestConfirmation [confirmationId=" + confirmationId + ", confirmationDate=" + confirmationDate
				+ ", request=" + request + "]";
	}
	public RequestConfirmation(int confirmationId, Date confirmationDate, Request request) {
		super();
		this.confirmationId = confirmationId;
		this.confirmationDate = confirmationDate;
		this.request = request;
	}
	public RequestConfirmation() {
		super();
	}
	
	

}
