package com.revature.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A model representing the confirmation of awarding reimbursement
 * @author ca132
 *
 */
@Entity
@Table(name = "reimbursement_confirmation", schema = "\"ExpenseReimbursementManagementSystem\"")
public class ReimbursementConfirmation {
	
	/**
	 * The confirmationId is the unique numeric identifier for a reimbursement's confirmation
	 */
	@Id
	@Column
	private int confirmationId;
	/**
	 * The confirmationDate is the date when the reimbursement confirmation was created
	 */
	@Column
	private Date confirmationDate;
	/**
	 * The reimbursementAwarded is the information associated with the reimbursement 
	 */
	@ManyToOne
	@JoinColumn(name = "reimbursementId")
	private Reimbursement reimbursementAwarded;
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
	public Reimbursement getReimbursementAwarded() {
		return reimbursementAwarded;
	}
	public void setReimbursementAwarded(Reimbursement reimbursementAwarded) {
		this.reimbursementAwarded = reimbursementAwarded;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((confirmationDate == null) ? 0 : confirmationDate.hashCode());
		result = prime * result + confirmationId;
		result = prime * result + ((reimbursementAwarded == null) ? 0 : reimbursementAwarded.hashCode());
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
		ReimbursementConfirmation other = (ReimbursementConfirmation) obj;
		if (confirmationDate == null) {
			if (other.confirmationDate != null)
				return false;
		} else if (!confirmationDate.equals(other.confirmationDate))
			return false;
		if (confirmationId != other.confirmationId)
			return false;
		if (reimbursementAwarded == null) {
			if (other.reimbursementAwarded != null)
				return false;
		} else if (!reimbursementAwarded.equals(other.reimbursementAwarded))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ReimbursementConfirmation [confirmationId=" + confirmationId + ", confirmationDate=" + confirmationDate
				+ ", reimbursementAwarded=" + reimbursementAwarded + "]";
	}
	public ReimbursementConfirmation(int confirmationId, Date confirmationDate,
			Reimbursement reimbursementAwarded) {
		super();
		this.confirmationId = confirmationId;
		this.confirmationDate = confirmationDate;
		this.reimbursementAwarded = reimbursementAwarded;
	}
	public ReimbursementConfirmation() {
		super();
	}

}
