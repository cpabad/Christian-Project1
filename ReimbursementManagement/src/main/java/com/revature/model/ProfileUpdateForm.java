package com.revature.model;

import com.revature.util.FlowTrace;

/**
 * The profile-update form as submitted: three optional sections (username,
 * password, email), each activated by its non-blank confirm field.
 */
public class ProfileUpdateForm {

	private String oldUsername;
	private String newUsername;
	private String confirmUsername;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;
	private String oldEmail;
	private String newEmail;
	private String confirmEmail;

	public ProfileUpdateForm(String oldUsername, String newUsername, String confirmUsername,
			String oldPassword, String newPassword, String confirmPassword,
			String oldEmail, String newEmail, String confirmEmail) {
		FlowTrace.log(ProfileUpdateForm.class, "constructor (oldUsername, newUsername, confirmUsername, oldPassword, newPassword, confirmPassword, oldEmail, newEmail, confirmEmail) fired");
		this.oldUsername = oldUsername;
		this.newUsername = newUsername;
		this.confirmUsername = confirmUsername;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
		this.oldEmail = oldEmail;
		this.newEmail = newEmail;
		this.confirmEmail = confirmEmail;
	}

	public String getOldUsername() {
		return oldUsername;
	}

	public String getNewUsername() {
		return newUsername;
	}

	public String getConfirmUsername() {
		return confirmUsername;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public String getOldEmail() {
		return oldEmail;
	}

	public String getNewEmail() {
		return newEmail;
	}

	public String getConfirmEmail() {
		return confirmEmail;
	}

}
