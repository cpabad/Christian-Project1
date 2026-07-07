package com.revature.service;

import javax.persistence.NoResultException;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.ObjectNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.revature.model.ProfileUpdateForm;
import com.revature.model.ProfileUpdateOutcome;
import com.revature.model.Role;
import com.revature.model.User;
import com.revature.repository.UserRepositoryImpl;

public class UserServiceTest {
	
	@InjectMocks private static UserService userService;
	@Mock private static UserRepositoryImpl userRepository;
	
	@BeforeClass
	public static void setupBeforeClass() {
		userService = new UserService();
	}
	
	@Before
	public void setupBeforeEachMethod() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testFindById() {
		Mockito.when(userRepository.findById(1)).thenReturn(new User(1, "Major", "GhostInTheShell", "Matoko", "Kusanagi", "mk@section9.com", new Role(1, "Being a badass")));
		User retrievedUser = userService.findById(1);
		Assert.assertEquals("Being a badass", retrievedUser.getRole().getRole());
	}
	
	@Test
	public void testFindByUsername() {
		Mockito.when(userRepository.findByUsername("Major")).thenReturn(new User(1, "Major", "GhostInTheShell", "Matoko", "Kusanagi", "mk@section9.com", new Role(1, "Being a badass")));
		User retrievedUser = userService.findByUsername("Major");
		Assert.assertEquals("Being a badass", retrievedUser.getRole().getRole());
	}
	
	@Test
	public void testFindByEmail() {
		Mockito.when(userRepository.findByEmail("mk@section9.com")).thenReturn(new User(1, "Major", "GhostInTheShell", "Matoko", "Kusanagi", "mk@section9.com", new Role(1, "Being a badass")));
		User retrievedUser = userService.findByEmail("mk@section9.com");
		Assert.assertEquals("Being a badass", retrievedUser.getRole().getRole());
	}

	@Test
	public void testUpdate() {
		User user = new User(1, "Major", "GhostInTheShell", "Matoko", "Kusanagi", "mk@section9.com", new Role(1, "Being a badass"));
		userService.update(user);
		// UserService.update delegates to the repository's updateEmployee(...)
		Mockito.verify(userRepository).updateEmployee(user);
	}

	// --- authenticate ---

	private static User userWithPassword(String rawPassword) {
		return new User(1, "major", new BCryptPasswordEncoder().encode(rawPassword), "Matoko", "Kusanagi", "mk@section9.com", new Role(2, "Employee"));
	}

	@Test
	public void testAuthenticateByUsername() {
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findByUsername("major")).thenReturn(user);
		Assert.assertSame(user, userService.authenticate("major", "pw"));
	}

	@Test
	public void testAuthenticateByEmail() {
		// an identifier containing "@" is looked up as an email address
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findByEmail("mk@section9.com")).thenReturn(user);
		Assert.assertSame(user, userService.authenticate("mk@section9.com", "pw"));
		Mockito.verify(userRepository, Mockito.never()).findByUsername(Mockito.anyString());
	}

	@Test
	public void testAuthenticateUsernameContainingDomainSuffix() {
		// regression pin: "@" alone decides the lookup - a username like "x.net"
		// must go to the username lookup (the old heuristic routed it to email)
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findByUsername("x.net")).thenReturn(user);
		Assert.assertSame(user, userService.authenticate("x.net", "pw"));
		Mockito.verify(userRepository, Mockito.never()).findByEmail(Mockito.anyString());
	}

	@Test
	public void testAuthenticateUnknownUser() {
		Mockito.when(userRepository.findByUsername("ghost")).thenThrow(new NoResultException());
		Assert.assertNull(userService.authenticate("ghost", "pw"));
	}

	@Test
	public void testAuthenticateNullUser() {
		// repository returning null (rather than throwing) also fails closed
		Assert.assertNull(userService.authenticate("ghost", "pw"));
	}

	@Test
	public void testAuthenticateWrongPassword() {
		Mockito.when(userRepository.findByUsername("major")).thenReturn(userWithPassword("right"));
		Assert.assertNull(userService.authenticate("major", "wrong"));
	}

	@Test
	public void testAuthenticateBlankPassword() {
		Mockito.when(userRepository.findByUsername("major")).thenReturn(userWithPassword("pw"));
		Assert.assertNull(userService.authenticate("major", "   "));
	}

	// --- updateProfile ---

	private static ProfileUpdateForm usernameForm(String oldUsername, String newUsername) {
		return new ProfileUpdateForm(oldUsername, newUsername, "y", "", "", "", "", "", "");
	}

	private static ProfileUpdateForm passwordForm(String oldPassword, String newPassword) {
		return new ProfileUpdateForm("", "", "", oldPassword, newPassword, "y", "", "", "");
	}

	private static ProfileUpdateForm emailForm(String oldEmail, String newEmail) {
		return new ProfileUpdateForm("", "", "", "", "", "", oldEmail, newEmail, "y");
	}

	@Test
	public void testUpdateProfileUsername() {
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);
		Mockito.when(userRepository.findByUsername("kusanagi")).thenThrow(new NoResultException());

		ProfileUpdateOutcome outcome = userService.updateProfile(1, usernameForm("major", "kusanagi"));

		Assert.assertEquals(ProfileUpdateOutcome.UPDATED, outcome);
		Assert.assertEquals("kusanagi", user.getUsername());
		Mockito.verify(userRepository).updateEmployee(user);
	}

	@Test
	public void testUpdateProfileUsernameTakenIsSkipped() {
		// quirk pin: if the new username already exists, the section is silently
		// skipped - the update still "succeeds" with the username unchanged
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);
		Mockito.when(userRepository.findByUsername("kusanagi")).thenReturn(userWithPassword("other"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, usernameForm("major", "kusanagi"));

		Assert.assertEquals(ProfileUpdateOutcome.UPDATED, outcome);
		Assert.assertEquals("major", user.getUsername());
		Mockito.verify(userRepository).updateEmployee(user);
	}

	@Test
	public void testUpdateProfileUsernameNonUnique() {
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);
		Mockito.when(userRepository.findByUsername("kusanagi")).thenThrow(new NonUniqueObjectException("", 1, "User"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, usernameForm("major", "kusanagi"));

		Assert.assertEquals(ProfileUpdateOutcome.INVALID_ENTRIES, outcome);
		Mockito.verify(userRepository, Mockito.never()).updateEmployee(Mockito.any(User.class));
	}

	@Test
	public void testUpdateProfileWrongOldUsername() {
		Mockito.when(userRepository.findById(1)).thenReturn(userWithPassword("pw"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, usernameForm("impostor", "kusanagi"));

		Assert.assertEquals(ProfileUpdateOutcome.INVALID_ENTRIES, outcome);
		Mockito.verify(userRepository, Mockito.never()).updateEmployee(Mockito.any(User.class));
	}

	@Test
	public void testUpdateProfilePassword() {
		User user = userWithPassword("oldpw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);

		ProfileUpdateOutcome outcome = userService.updateProfile(1, passwordForm("oldpw", "newpw"));

		Assert.assertEquals(ProfileUpdateOutcome.UPDATED, outcome);
		Assert.assertTrue(new BCryptPasswordEncoder().matches("newpw", user.getPassword()));
		Mockito.verify(userRepository).updateEmployee(user);
	}

	@Test
	public void testUpdateProfileWrongOldPassword() {
		Mockito.when(userRepository.findById(1)).thenReturn(userWithPassword("oldpw"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, passwordForm("wrong", "newpw"));

		Assert.assertEquals(ProfileUpdateOutcome.INVALID_ENTRIES, outcome);
		Mockito.verify(userRepository, Mockito.never()).updateEmployee(Mockito.any(User.class));
	}

	@Test
	public void testUpdateProfileEmail() {
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);
		Mockito.when(userRepository.findByEmail("new@section9.com")).thenThrow(new NoResultException());

		ProfileUpdateOutcome outcome = userService.updateProfile(1, emailForm("mk@section9.com", "new@section9.com"));

		Assert.assertEquals(ProfileUpdateOutcome.UPDATED, outcome);
		Assert.assertEquals("new@section9.com", user.getEmail());
		Mockito.verify(userRepository).updateEmployee(user);
	}

	@Test
	public void testUpdateProfileEmailNonUnique() {
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);
		Mockito.when(userRepository.findByEmail("new@section9.com")).thenThrow(new NonUniqueObjectException("", 1, "User"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, emailForm("mk@section9.com", "new@section9.com"));

		Assert.assertEquals(ProfileUpdateOutcome.INVALID_ENTRIES, outcome);
		Mockito.verify(userRepository, Mockito.never()).updateEmployee(Mockito.any(User.class));
	}

	@Test
	public void testUpdateProfileEmailTakenIsSkipped() {
		// quirk pin (mirror of the username case): a taken email is silently
		// skipped and the update still reports success
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);
		Mockito.when(userRepository.findByEmail("new@section9.com")).thenReturn(userWithPassword("other"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, emailForm("mk@section9.com", "new@section9.com"));

		Assert.assertEquals(ProfileUpdateOutcome.UPDATED, outcome);
		Assert.assertEquals("mk@section9.com", user.getEmail());
		Mockito.verify(userRepository).updateEmployee(user);
	}

	@Test
	public void testUpdateProfileWrongOldEmail() {
		Mockito.when(userRepository.findById(1)).thenReturn(userWithPassword("pw"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, emailForm("wrong@section9.com", "new@section9.com"));

		Assert.assertEquals(ProfileUpdateOutcome.INVALID_ENTRIES, outcome);
		Mockito.verify(userRepository, Mockito.never()).updateEmployee(Mockito.any(User.class));
	}

	@Test
	public void testUpdateProfileBlankNewEmailSkipsSection() {
		// quirk pin: a confirmed email section with a blank new address does
		// nothing, but the update still runs (and reports success)
		User user = userWithPassword("pw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);

		ProfileUpdateOutcome outcome = userService.updateProfile(1, emailForm("mk@section9.com", "   "));

		Assert.assertEquals(ProfileUpdateOutcome.UPDATED, outcome);
		Assert.assertEquals("mk@section9.com", user.getEmail());
		Mockito.verify(userRepository).updateEmployee(user);
	}

	@Test
	public void testUpdateProfileAllSectionsAtOnce() {
		// username + email via the ObjectNotFoundException flavor of "not taken"
		// (the NoResultException flavor is covered by the single-section tests)
		User user = userWithPassword("oldpw");
		Mockito.when(userRepository.findById(1)).thenReturn(user);
		Mockito.when(userRepository.findByUsername("kusanagi")).thenThrow(new ObjectNotFoundException(1, "User"));
		Mockito.when(userRepository.findByEmail("new@section9.com")).thenThrow(new ObjectNotFoundException(1, "User"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, new ProfileUpdateForm(
				"major", "kusanagi", "y",
				"oldpw", "newpw", "y",
				"mk@section9.com", "new@section9.com", "y"));

		Assert.assertEquals(ProfileUpdateOutcome.UPDATED, outcome);
		Assert.assertEquals("kusanagi", user.getUsername());
		Assert.assertEquals("new@section9.com", user.getEmail());
		Assert.assertTrue(new BCryptPasswordEncoder().matches("newpw", user.getPassword()));
		Mockito.verify(userRepository).updateEmployee(user);
	}

	@Test
	public void testUpdateProfileNoEntries() {
		Mockito.when(userRepository.findById(1)).thenReturn(userWithPassword("pw"));

		ProfileUpdateOutcome outcome = userService.updateProfile(1, new ProfileUpdateForm("", "", "", "", "", "", "", "", ""));

		Assert.assertEquals(ProfileUpdateOutcome.NO_ENTRIES, outcome);
		Mockito.verify(userRepository, Mockito.never()).updateEmployee(Mockito.any(User.class));
	}

}
