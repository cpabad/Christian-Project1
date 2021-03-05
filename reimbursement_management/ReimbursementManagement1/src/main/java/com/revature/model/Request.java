package com.revature.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A model representing an expense reimbursement request.
 * @author ca132
 *
 */
@Entity
@Table(name = "request", schema = "\"ExpenseReimbursementManagementSystem\"")
public class Request {
	
	/**
	 * The requestId field is the unique numeric identifier for a specific request.
	 */
	@Column
	@Id
	private int requestId;
	/**
	 * The amount field is the monetary amount requested.
	 */
	@Column
	private double amount;
	/**
	 * The eventDate field is the date when the requester attended the event.
	 */
	@Column
	private Date eventDate;
	/**
	 * The eventLocation field is the event location.
	 */
	@ManyToOne
	@JoinColumn(name = "eventLocation")
	private EventLocation eventLocation;
	/**
	 * The requestedEvent field is the name/activities of the event attended.
	 */
	@Column
	private String requestedEvent;
	/**
	 * The requesterUserId field is the creator of this reimbursement request.
	 */
	@ManyToOne
	@JoinColumn(name = "requesterUserId")
	private User requester;
	/**
	 * The requestStatus field is the current status of this request.
	 */
	@ManyToOne
	@JoinColumn(name = "statusId")
	private RequestStatus requestStatus;
	public int getRequestId() {
		return requestId;
	}
	public double getAmount() {
		return amount;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public EventLocation getEventLocation() {
		return eventLocation;
	}
	public String getRequestedEvent() {
		return requestedEvent;
	}
	public User getRequester() {
		return requester;
	}
	public RequestStatus getRequestStatus() {
		return requestStatus;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	public void setEventLocation(EventLocation eventLocation) {
		this.eventLocation = eventLocation;
	}
	public void setRequestedEvent(String requestedEvent) {
		this.requestedEvent = requestedEvent;
	}
	public void setRequester(User requester) {
		this.requester = requester;
	}
	public void setRequestStatus(RequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((eventDate == null) ? 0 : eventDate.hashCode());
		result = prime * result + ((eventLocation == null) ? 0 : eventLocation.hashCode());
		result = prime * result + requestId;
		result = prime * result + ((requestStatus == null) ? 0 : requestStatus.hashCode());
		result = prime * result + ((requestedEvent == null) ? 0 : requestedEvent.hashCode());
		result = prime * result + ((requester == null) ? 0 : requester.hashCode());
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
		Request other = (Request) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (eventDate == null) {
			if (other.eventDate != null)
				return false;
		} else if (!eventDate.equals(other.eventDate))
			return false;
		if (eventLocation == null) {
			if (other.eventLocation != null)
				return false;
		} else if (!eventLocation.equals(other.eventLocation))
			return false;
		if (requestId != other.requestId)
			return false;
		if (requestStatus == null) {
			if (other.requestStatus != null)
				return false;
		} else if (!requestStatus.equals(other.requestStatus))
			return false;
		if (requestedEvent == null) {
			if (other.requestedEvent != null)
				return false;
		} else if (!requestedEvent.equals(other.requestedEvent))
			return false;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Request [requestId=" + requestId + ", amount=" + amount + ", eventDate=" + eventDate
				+ ", eventLocation=" + eventLocation + ", requestedEvent=" + requestedEvent + ", requester=" + requester
				+ ", requestStatus=" + requestStatus + "]";
	}
	public Request() {
		super();
	}
	public Request(int requestId, double amount, Date eventDate, EventLocation eventLocation, String requestedEvent,
			User requester, RequestStatus requestStatus) {
		super();
		this.requestId = requestId;
		this.amount = amount;
		this.eventDate = eventDate;
		this.eventLocation = eventLocation;
		this.requestedEvent = requestedEvent;
		this.requester = requester;
		this.requestStatus = requestStatus;
	}
	
	

}
