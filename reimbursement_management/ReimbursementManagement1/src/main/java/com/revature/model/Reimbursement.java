package com.revature.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A model representing an amount rewarded for an approved reimbursement request.
 * @author ca132
 *
 */
@Entity
@Table(name = "reimbursement", schema = "\"ExpenseReimbursementManagementSystem\"")
public class Reimbursement {
	
	/**
	 * The awardId field is the unique numeric identifier for a reimbursement
	 */
	@Id
	@Column
	private int reimbursementId;
	/**
	 * The amount field is the monetary amount gifted to the reimbursement requester
	 */
	@Column
	private double amount;
	/**
	 * The dateAwarded field is the date when the reimbursement amount was sent to the requester
	 */
	@Column
	private Date dateAwarded;
	/**
	 * The finalApproval field is the last approval needed for a reimbursement request
	 */
	@ManyToOne
	@JoinColumn(name = "finalApprovalId")
	private SupervisorApproval finalApproval;
	/**
	 * The reimbursementStatus field is the status of the reimbursement. 
	 */
	@ManyToOne
	@JoinColumn(name = "statusId")
	private ReimbursementStatus reimbursementStatus;
	public int getReimbursementId() {
		return reimbursementId;
	}
	public double getAmount() {
		return amount;
	}
	public Date getDateAwarded() {
		return dateAwarded;
	}
	public SupervisorApproval getFinalApproval() {
		return finalApproval;
	}
	public ReimbursementStatus getReimbursementStatus() {
		return reimbursementStatus;
	}
	public void setReimbursementId(int reimbursementId) {
		this.reimbursementId = reimbursementId;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public void setDateAwarded(Date dateAwarded) {
		this.dateAwarded = dateAwarded;
	}
	public void setFinalApproval(SupervisorApproval finalApproval) {
		this.finalApproval = finalApproval;
	}
	public void setReimbursementStatus(ReimbursementStatus reimbursementStatus) {
		this.reimbursementStatus = reimbursementStatus;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((dateAwarded == null) ? 0 : dateAwarded.hashCode());
		result = prime * result + ((finalApproval == null) ? 0 : finalApproval.hashCode());
		result = prime * result + reimbursementId;
		result = prime * result + ((reimbursementStatus == null) ? 0 : reimbursementStatus.hashCode());
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
		Reimbursement other = (Reimbursement) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (dateAwarded == null) {
			if (other.dateAwarded != null)
				return false;
		} else if (!dateAwarded.equals(other.dateAwarded))
			return false;
		if (finalApproval == null) {
			if (other.finalApproval != null)
				return false;
		} else if (!finalApproval.equals(other.finalApproval))
			return false;
		if (reimbursementId != other.reimbursementId)
			return false;
		if (reimbursementStatus == null) {
			if (other.reimbursementStatus != null)
				return false;
		} else if (!reimbursementStatus.equals(other.reimbursementStatus))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Reimbursement [reimbursementId=" + reimbursementId + ", amount=" + amount + ", dateAwarded="
				+ dateAwarded + ", finalApproval=" + finalApproval + ", reimbursementStatus=" + reimbursementStatus
				+ "]";
	}
	public Reimbursement(int reimbursementId, double amount, Date dateAwarded, SupervisorApproval finalApproval,
			ReimbursementStatus reimbursementStatus) {
		super();
		this.reimbursementId = reimbursementId;
		this.amount = amount;
		this.dateAwarded = dateAwarded;
		this.finalApproval = finalApproval;
		this.reimbursementStatus = reimbursementStatus;
	}
	public Reimbursement() {
		super();
	}
	

}
