package com.revature.model;

/**
 * Where a reimbursement request stands after a manager's approve/deny decision
 * is applied to their supervisor approval.
 */
public enum ApprovalOutcome {
	/** Final approval reached: request and reimbursement are resolved as approved. */
	APPROVED,
	/** Denial is final: request and reimbursement are resolved as denied. */
	DENIED,
	/** This manager approved, but a supervisor above still has to decide. */
	ESCALATED,
	/** Peer managers below still have pending decisions; nothing was persisted. */
	WAITING_ON_OTHERS
}
