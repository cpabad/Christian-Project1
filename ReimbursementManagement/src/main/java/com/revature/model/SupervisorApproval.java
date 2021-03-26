package com.revature.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This model represents the approval made to a request.
 * @author ca132
 *
 */
@Entity
@Table(name = "supervisor_approval", schema = "\"ExpenseReimbursementManagementSystem\"")
public class SupervisorApproval {
	
	/**
	 * The approvalId field is the unique numeric identifier of a supervisor's approval
	 */
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int approvalId;
	/**
	 * The datePreviouslyUpdated field is the date when the latest update was made to an approval
	 */
	@Column(name = "dateOfPreviousUpdate")
	private Date datePreviouslyUpdated;
	/**
	 * The request field is the request to be approved
	 */
	@ManyToOne
	@JoinColumn(name = "requestId")
	private Request request;
	/**
	 * The hierarchy field is the supervisor-employee relationship of an approval
	 */
	@ManyToOne
	@JoinColumn(name = "hierarchyId")
	private Hierarchy hierarchy;
	/**
	 * The supervisorApprovalStatus is the status of an approval
	 */
	@ManyToOne
	@JoinColumn(name = "statusId")
	private SupervisorApprovalStatus supervisorApprovalStatus;
	/**
	 * The approval field is the decision whether a request can move onto the next round of approval
	 */
	@Column
	private boolean approval;
	public int getApprovalId() {
		return approvalId;
	}
	public void setApprovalId(int approvalId) {
		this.approvalId = approvalId;
	}
	public Date getDatePreviouslyUpdated() {
		return datePreviouslyUpdated;
	}
	public void setDatePreviouslyUpdated(Date datePreviouslyUpdated) {
		this.datePreviouslyUpdated = datePreviouslyUpdated;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	public Hierarchy getHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
	public SupervisorApprovalStatus getSupervisorApprovalStatus() {
		return supervisorApprovalStatus;
	}
	public void setSupervisorApprovalStatus(SupervisorApprovalStatus supervisorApprovalStatus) {
		this.supervisorApprovalStatus = supervisorApprovalStatus;
	}
	public boolean isApproval() {
		return approval;
	}
	public void setApproval(boolean approval) {
		this.approval = approval;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (approval ? 1231 : 1237);
		result = prime * result + approvalId;
		result = prime * result + ((datePreviouslyUpdated == null) ? 0 : datePreviouslyUpdated.hashCode());
		result = prime * result + ((hierarchy == null) ? 0 : hierarchy.hashCode());
		result = prime * result + ((request == null) ? 0 : request.hashCode());
		result = prime * result + ((supervisorApprovalStatus == null) ? 0 : supervisorApprovalStatus.hashCode());
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
		SupervisorApproval other = (SupervisorApproval) obj;
		if (approval != other.approval)
			return false;
		if (approvalId != other.approvalId)
			return false;
		if (datePreviouslyUpdated == null) {
			if (other.datePreviouslyUpdated != null)
				return false;
		} else if (!datePreviouslyUpdated.equals(other.datePreviouslyUpdated))
			return false;
		if (hierarchy == null) {
			if (other.hierarchy != null)
				return false;
		} else if (!hierarchy.equals(other.hierarchy))
			return false;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		if (supervisorApprovalStatus == null) {
			if (other.supervisorApprovalStatus != null)
				return false;
		} else if (!supervisorApprovalStatus.equals(other.supervisorApprovalStatus))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SupervisorApproval [approvalId=" + approvalId + ", datePreviouslyUpdated=" + datePreviouslyUpdated
				+ ", request=" + request + ", hierarchy=" + hierarchy + ", supervisorApprovalStatus="
				+ supervisorApprovalStatus + ", approval=" + approval + "]";
	}
	public SupervisorApproval(int approvalId, Date datePreviouslyUpdated, Request request, Hierarchy hierarchy,
			SupervisorApprovalStatus supervisorApprovalStatus, boolean approval) {
		super();
		this.approvalId = approvalId;
		this.datePreviouslyUpdated = datePreviouslyUpdated;
		this.request = request;
		this.hierarchy = hierarchy;
		this.supervisorApprovalStatus = supervisorApprovalStatus;
		this.approval = approval;
	}
	public SupervisorApproval() {
		super();
	}

}
