package com.revature.model;

/**
 * Result of a profile-update attempt (username / password / email sections).
 */
public enum ProfileUpdateOutcome {
	/** At least one section validated and the user row was updated. */
	UPDATED,
	/** A section failed validation (wrong current value, or the new value is taken). */
	INVALID_ENTRIES,
	/** No section was confirmed - there was nothing to update. */
	NO_ENTRIES
}
