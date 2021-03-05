package com.revature.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This is a model of confirmation of a supervisor's approval
 * @author ca132
 *
 */
@Entity
@Table(name = "approval_confirmation", schema = "\"ExpenseReimbursementManagementSystem\"")
public class SupervisorApprovalConfirmation {
	
	/**
	 * The confirmationId field is a unique numeric identifier of an approval's confirmation
	 */
	@Id
	@Column
	private int confirmationId;
	/**
	 * The confirmationDate field is the date of an approval's confirmation
	 */
	@Column
	private Date confirmationDate;
	/**
	 * The approval field is the approval information associated with an approval's confirmation
	 */
	@ManyToOne
	@JoinColumn(name = "approvalId")
	private SupervisorApproval approval;
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
	public SupervisorApproval getApproval() {
		return approval;
	}
	public void setApproval(SupervisorApproval approval) {
		this.approval = approval;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((approval == null) ? 0 : approval.hashCode());
		result = prime * result + ((confirmationDate == null) ? 0 : confirmationDate.hashCode());
		result = prime * result + confirmationId;
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
		SupervisorApprovalConfirmation other = (SupervisorApprovalConfirmation) obj;
		if (approval == null) {
			if (other.approval != null)
				return false;
		} else if (!approval.equals(other.approval))
			return false;
		if (confirmationDate == null) {
			if (other.confirmationDate != null)
				return false;
		} else if (!confirmationDate.equals(other.confirmationDate))
			return false;
		if (confirmationId != other.confirmationId)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ApprovalConfirmation [confirmationId=" + confirmationId + ", confirmationDate=" + confirmationDate
				+ ", approval=" + approval + "]";
	}
	public SupervisorApprovalConfirmation(int confirmationId, Date confirmationDate, SupervisorApproval approval) {
		super();
		this.confirmationId = confirmationId;
		this.confirmationDate = confirmationDate;
		this.approval = approval;
	}
	public SupervisorApprovalConfirmation() {
		super();
	}

}
