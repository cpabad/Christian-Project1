package com.revature.service;

import java.nio.charset.StandardCharsets;

import javax.persistence.NoResultException;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.revature.model.ProfileUpdateForm;
import com.revature.model.ProfileUpdateOutcome;
import com.revature.model.User;
import com.revature.repository.UserRepository;
import com.revature.repository.UserRepositoryImpl;
import com.revature.util.FlowTrace;

public class UserService {
	
	private UserRepository userRepository;
	
	public UserService() {
		userRepository = new UserRepositoryImpl();
	}
	
	public User findById(int id) {
		FlowTrace.log(UserService.class, "findById: service operation begins");
		return this.userRepository.findById(id);
	}
	
	public User findByUsername(String username) {
		FlowTrace.log(UserService.class, "findByUsername: service operation begins");
		return this.userRepository.findByUsername(username);
	}
	
	public User findByEmail(String email) {
		FlowTrace.log(UserService.class, "findByEmail: service operation begins");
		return this.userRepository.findByEmail(email);
	}
	
	public void update(User user) {
		FlowTrace.log(UserService.class, "update: service operation begins");
		this.userRepository.updateEmployee(user);
	}

	/**
	 * Looks up a user by username or email (an identifier containing "@" is an
	 * email address) and verifies the raw password against the stored BCrypt
	 * hash. Returns the user on success and null on any failure - unknown
	 * identifier, blank password, or wrong password - so the caller cannot
	 * distinguish which check failed.
	 */
	public User authenticate(String usernameOrEmail, String rawPassword) {
		FlowTrace.log(UserService.class, "authenticate: identifier '" + usernameOrEmail + "' "
				+ (usernameOrEmail.contains("@") ? "contains '@' - treating it as an email" : "has no '@' - treating it as a username"));
		User user;
		try {
			if(usernameOrEmail.contains("@")) {
				user = this.userRepository.findByEmail(usernameOrEmail);
			} else {
				user = this.userRepository.findByUsername(usernameOrEmail);
			}
		} catch (NoResultException e) {
			FlowTrace.log(UserService.class, "authenticate outcome: no user row for that identifier (NoResultException) - returning null");
			return null;
		}
		if(user == null || rawPassword.trim().isEmpty()) {
			FlowTrace.log(UserService.class, "authenticate outcome: " + (user == null ? "repository returned null" : "blank password submitted") + " - returning null");
			return null;
		}
		// Maximum password length established per CVE-2025-22228: BCrypt reads only the first
		// 72 bytes of its input, so matches() would return true for ANY longer password whose
		// first 72 bytes match. Reject oversized input before it can reach BCrypt.
		// https://avd.aquasec.com/nvd/cve-2025-22228
		if(exceedsBCrypt72ByteLimit(rawPassword)) {
			FlowTrace.log(UserService.class, "authenticate outcome: password exceeds BCrypt's 72-byte input limit (CVE-2025-22228 guard) - returning null");
			return null;
		}
		if(new BCryptPasswordEncoder().matches(rawPassword, user.getPassword())) {
			FlowTrace.log(UserService.class, "authenticate outcome: BCrypt verified the password for '" + user.getUsername() + "' - returning the user");
			return user;
		}
		FlowTrace.log(UserService.class, "authenticate outcome: BCrypt password mismatch for '" + user.getUsername() + "' - returning null");
		return null;
	}

	/**
	 * Applies the confirmed sections of a profile-update form to the user.
	 * Each section requires the current value to match before the new value is
	 * accepted; username and email additionally check that the new value is not
	 * already taken (a taken value is skipped silently - the section simply does
	 * not change, matching the original behavior). Validation failures return
	 * without persisting anything.
	 */
	public ProfileUpdateOutcome updateProfile(int userId, ProfileUpdateForm form) {
		FlowTrace.log(UserService.class, "updateProfile: service operation begins");
		User user = this.userRepository.findById(userId);
		if(form.getConfirmUsername().trim().isEmpty() == false) {
			if(form.getOldUsername().trim().isEmpty() == false && user.getUsername().equals(form.getOldUsername())) {
				try {
					this.userRepository.findByUsername(form.getNewUsername());
				} catch (NonUniqueObjectException e) {
					return ProfileUpdateOutcome.INVALID_ENTRIES;
				} catch (ObjectNotFoundException e) {
					user.setUsername(form.getNewUsername());
				} catch (NoResultException e) {
					user.setUsername(form.getNewUsername());
				}
			} else {
				return ProfileUpdateOutcome.INVALID_ENTRIES;
			}
		}
		if(form.getConfirmPassword().trim().isEmpty() == false) {
			// Same 72-byte maximum as authenticate, per CVE-2025-22228: neither the old
			// password (BCrypt matches) nor the new one (BCrypt encode) may exceed the limit,
			// or BCrypt silently truncates it. https://avd.aquasec.com/nvd/cve-2025-22228
			if(exceedsBCrypt72ByteLimit(form.getOldPassword()) || exceedsBCrypt72ByteLimit(form.getNewPassword())) {
				return ProfileUpdateOutcome.INVALID_ENTRIES;
			}
			if(form.getOldPassword() != null && new BCryptPasswordEncoder().matches(form.getOldPassword(), user.getPassword())) {
				user.setPassword(new BCryptPasswordEncoder().encode(form.getNewPassword()));
			} else {
				return ProfileUpdateOutcome.INVALID_ENTRIES;
			}
		}
		if(form.getConfirmEmail().trim().isEmpty() == false) {
			if(form.getOldEmail() != null && user.getEmail().equals(form.getOldEmail())) {
				if(form.getNewEmail().trim().isEmpty() == false) {
					try {
						this.userRepository.findByEmail(form.getNewEmail());
					} catch (NonUniqueObjectException e) {
						return ProfileUpdateOutcome.INVALID_ENTRIES;
					} catch (ObjectNotFoundException e) {
						user.setEmail(form.getNewEmail());
					} catch (NoResultException e) {
						user.setEmail(form.getNewEmail());
					}
				}
			} else {
				return ProfileUpdateOutcome.INVALID_ENTRIES;
			}
		}
		if(form.getConfirmUsername().trim().isEmpty() && form.getConfirmPassword().trim().isEmpty() && form.getConfirmEmail().trim().isEmpty()) {
			return ProfileUpdateOutcome.NO_ENTRIES;
		}
		this.userRepository.updateEmployee(user);
		return ProfileUpdateOutcome.UPDATED;
	}

	// BCrypt's algorithm reads at most 72 bytes; spring-security-crypto truncates anything
	// beyond that without error (CVE-2025-22228). Byte length, not char length: multi-byte
	// UTF-8 characters hit the limit sooner than String.length() suggests.
	private static boolean exceedsBCrypt72ByteLimit(String password) {
		return password != null && password.getBytes(StandardCharsets.UTF_8).length > 72;
	}

}
