package com.revature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A model representing the status of a reimbursement request.
 * @author ca132
 *
 */
@Entity
@Table(name = "request_status", schema = "\"ExpenseReimbursementManagementSystem\"")
public class RequestStatus {
	
	/**
	 * The statusId is the unique numeric identifier of a specific request status.
	 */
	@Column
	@Id
	private int statusId;
	/**
	 * The status is the String value of a specific request status.
	 */
	@Column
	private String status;
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + statusId;
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
		RequestStatus other = (RequestStatus) obj;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (statusId != other.statusId)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RequestStatus [statusId=" + statusId + ", status=" + status + "]";
	}
	public RequestStatus(int statusId, String status) {
		super();
		this.statusId = statusId;
		this.status = status;
	}
	public RequestStatus() {
		super();
	}
	

}
