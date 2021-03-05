package com.revature.model;

import java.sql.Date;

/**
 * A model representing an amount rewarded for an approved reimbursement request.
 * @author ca132
 *
 */
public class ReimbursementAwarded {
	
	/**
	 * The awardId field is the unique numeric identifier for a reimbursement
	 */
	private int awardId;
	/**
	 * The amount field is the monetary amount gifted to the reimbursement requester
	 */
	private double amount;
	/**
	 * The dateAwarded field is the date when the reimbursement amount was sent to the requester
	 */
	private Date dateAwarded;
	/**
	 * The finalApproval field is the last approval needed for a reimbursement request
	 */
	private SupervisorApproval finalApproval;
	/**
	 * The reimbursementStatus field is the status of the reimbursement. 
	 */
	private ReimbursementStatus reimbursementStatus;
	public int getAwardId() {
		return awardId;
	}
	public void setAwardId(int awardId) {
		this.awardId = awardId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getDateAwarded() {
		return dateAwarded;
	}
	public void setDateAwarded(Date dateAwarded) {
		this.dateAwarded = dateAwarded;
	}
	public SupervisorApproval getFinalApproval() {
		return finalApproval;
	}
	public void setFinalApproval(SupervisorApproval finalApproval) {
		this.finalApproval = finalApproval;
	}
	public ReimbursementStatus getReimbursementStatus() {
		return reimbursementStatus;
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
		result = prime * result + awardId;
		result = prime * result + ((dateAwarded == null) ? 0 : dateAwarded.hashCode());
		result = prime * result + ((finalApproval == null) ? 0 : finalApproval.hashCode());
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
		ReimbursementAwarded other = (ReimbursementAwarded) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (awardId != other.awardId)
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
		if (reimbursementStatus == null) {
			if (other.reimbursementStatus != null)
				return false;
		} else if (!reimbursementStatus.equals(other.reimbursementStatus))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ReimbursementAwarded [awardId=" + awardId + ", amount=" + amount + ", dateAwarded=" + dateAwarded
				+ ", finalApproval=" + finalApproval + ", reimbursementStatus=" + reimbursementStatus + "]";
	}
	public ReimbursementAwarded(int awardId, double amount, Date dateAwarded, SupervisorApproval finalApproval,
			ReimbursementStatus reimbursementStatus) {
		super();
		this.awardId = awardId;
		this.amount = amount;
		this.dateAwarded = dateAwarded;
		this.finalApproval = finalApproval;
		this.reimbursementStatus = reimbursementStatus;
	}
	public ReimbursementAwarded() {
		super();
	}

}
