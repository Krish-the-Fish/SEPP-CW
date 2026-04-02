package com.fortytwogroup.systemtests;

import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.model.FacultyMember;
import com.fortytwogroup.model.User;
import com.fortytwogroup.model.UserStorage;
import com.fortytwogroup.service.PasswordService;
import com.fortytwogroup.service.RegistrationUtility;
import com.fortytwogroup.view.TextUserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LoginSystemTests {
  private UserController userController;
  private TextUserInterface mockTextUserInterface;
  private UserStorage userStorage;
  private PasswordService passwordService;
  private RegistrationUtility registrationUtility;

  @BeforeEach
  void setUp() {
    mockTextUserInterface = Mockito.mock(TextUserInterface.class);
    userStorage = new UserStorage();
    passwordService = new PasswordService();
    registrationUtility = new RegistrationUtility(
        "src/main/resources/faculty.csv");
    userController = new UserController(
        userStorage, mockTextUserInterface, passwordService, registrationUtility);
  }

  @Test
  void testLogin_facultyMemberAddedToStorage() {
    when(mockTextUserInterface.getEmailInput()).thenReturn("johndoe@gmail.com");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("12345");

    userController.login();

    assertTrue(userStorage.checkIfEmailOnSystem("johndoe@gmail.com"),
        "Faculty member should be added to storage after first login attempt");
  }

  @Test
  void testLogin_logsInExistingUser() {
    // login non-existing faculty member first to create account for them
    when(mockTextUserInterface.getEmailInput()).thenReturn("johndoe@gmail.com");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("12345");
    userController.login();  // user should now be on system storage map

    // test that if they attempt to log in again there will be success since on system now
    when(mockTextUserInterface.getEmailInput()).thenReturn("johndoe@gmail.com");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("12345");
    User result = userController.login();

    assertNotNull(result,
        "Faculty member who has logged in once before should be able to log in "
            + "with credentials");
  }

  @Test
  void testLogin_existingUserWithValidCredentials() {
    // pre-add a user to storage as if they were already registered
    String hashedPassword = passwordService.hashPlainTextPassword("password123");
    User existingUser = new FacultyMember("existing@uni.ac.uk", hashedPassword);
    userStorage.addUserToMap("existing@uni.ac.uk", existingUser);

    when(mockTextUserInterface.getEmailInput()).thenReturn("existing@uni.ac.uk");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("password123");

    User result = userController.login();

    assertNotNull(result, "Existing user with correct password should be able to log in+"
        + "Doesn't need to be a faculty member to log in to existing account");
  }


  @Test
  void testLogin_invalidPassword() {
    String hashedPassword = passwordService.hashPlainTextPassword("password123");
    User existingUser = new FacultyMember("existing@uni.ac.uk", hashedPassword);
    userStorage.addUserToMap("existing@uni.ac.uk", existingUser);

    when(mockTextUserInterface.getEmailInput()).thenReturn("existing@uni.ac.uk");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("incorrectpassword");

    User result = userController.login();

    // faculty member trying to create account with wrong password should also be denied
    assertNull(result, "If attempting to log in with incorrect password but correct"
        + "email address, login attempt should be denied ");
  }

  @Test
  void testLogin_noEmailMatch() {
    String hashedPassword = passwordService.hashPlainTextPassword("password123");
    User existingUser = new FacultyMember("existing@uni.ac.uk", hashedPassword);
    userStorage.addUserToMap("existing@gmail.com", existingUser);

    when(mockTextUserInterface.getEmailInput()).thenReturn("wrongEmail@gmail.com");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("password123");

    User result = userController.login();

    assertNull(result, "If log in attempt has correct password but incorrect email"
        + "address to match, deny login attempt");
  }

  @Test
  void testLogin_wrongEmailAndPassword() {
    String hashedPassword = passwordService.hashPlainTextPassword("password123");
    User existingUser = new FacultyMember("existing@uni.ac.uk", hashedPassword);
    userStorage.addUserToMap("existing@gmail.com", existingUser);

    when(mockTextUserInterface.getEmailInput()).thenReturn("wrongEmail@gmail.com");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("incorrectpassword");

    User result = userController.login();

    assertNull(result, "If log in attempt has incorrect password and incorrect email"
        + "deny login attempt");
  }


  @Test
  void testLogin_nullEmailInput() {
    when(mockTextUserInterface.getEmailInput()).thenReturn(null);
    when(mockTextUserInterface.getPasswordInput()).thenReturn("password123");

    User result = userController.login();

    assertNull(result, "Null email input should return null");
  }


  @Test
  void testLogin_multipleUsersOnSystem() {
    String hashedPassword1 = passwordService.hashPlainTextPassword("password1");
    String hashedPassword2 = passwordService.hashPlainTextPassword("password2");
    String hashedPassword3 = passwordService.hashPlainTextPassword("password3");

    userStorage.addUserToMap("user1@uni.ac.uk", new FacultyMember("user1@uni.ac.uk", hashedPassword1));
    userStorage.addUserToMap("user2@uni.ac.uk", new FacultyMember("user2@uni.ac.uk", hashedPassword2));
    userStorage.addUserToMap("user3@uni.ac.uk", new FacultyMember("user3@uni.ac.uk", hashedPassword3));

    when(mockTextUserInterface.getEmailInput()).thenReturn("user2@uni.ac.uk");
    when(mockTextUserInterface.getPasswordInput()).thenReturn("password2");

    User result = userController.login();

    assertNotNull(result, "Correct user should be returned when multiple users are"
        + " on the system");
  }

}
