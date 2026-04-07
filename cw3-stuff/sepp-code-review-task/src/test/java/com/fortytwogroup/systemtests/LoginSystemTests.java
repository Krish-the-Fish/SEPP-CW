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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        "src/test/resources/faculty.csv");
    userController = new UserController(
        userStorage, mockTextUserInterface, passwordService, registrationUtility);
  }

  @Test
  void testLogin_facultyMemberAddedToStorage() {
    when(mockTextUserInterface.getUserInput(anyString()))
        .thenReturn("johndoe@gmail.com", "12345");

    userController.login();

    assertTrue(userStorage.checkIfEmailOnSystem("johndoe@gmail.com"),
        "Faculty member should be added to storage after first login attempt");
  }

  @Test
  void testLogin_logsInExistingUser() {
    // login non-existing faculty member first to create account for them
    when(mockTextUserInterface.getUserInput(anyString()))
        .thenReturn("johndoe@gmail.com", "12345", "johndoe@gmail.com", "12345");

    userController.login();  // user should now be on system storage map

    // test that if they attempt to log in again there will be success since on system now
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

    when(mockTextUserInterface.getUserInput(anyString())).
        thenReturn("existing@uni.ac.uk", "password123");

    User result = userController.login();

    assertNotNull(result, "Existing user with correct password should be able to log in+"
        + "Doesn't need to be a faculty member to log in to existing account");
  }


  @Test
  void testLogin_invalidPassword() {
    String hashedPassword = passwordService.hashPlainTextPassword("password123");
    User existingUser = new FacultyMember("existing@uni.ac.uk", hashedPassword);
    userStorage.addUserToMap("existing@uni.ac.uk", existingUser);

    when(mockTextUserInterface.getUserInput(anyString()))
        .thenReturn("existing@uni.ac.uk", "incorrectpassword")
        .thenThrow(new RuntimeException("Simulated exit after failed login"));

    assertThrows(RuntimeException.class, () -> userController.login());
  }

  @Test
  void testLogin_noEmailMatch() {
    String hashedPassword = passwordService.hashPlainTextPassword("password123");
    User existingUser = new FacultyMember("existing@uni.ac.uk", hashedPassword);
    userStorage.addUserToMap("existing@gmail.com", existingUser);

    when(mockTextUserInterface.getUserInput(anyString()))
        .thenReturn("wrongEmail@gmail.com", "password123")
        .thenThrow(new RuntimeException("Simulated exit after failed login"));

    assertThrows(RuntimeException.class, () -> userController.login());
  }

  @Test
  void testLogin_wrongEmailAndPassword() {
    String hashedPassword = passwordService.hashPlainTextPassword("password123");
    User existingUser = new FacultyMember("existing@uni.ac.uk", hashedPassword);
    userStorage.addUserToMap("existing@gmail.com", existingUser);

    when(mockTextUserInterface.getUserInput(anyString()))
        .thenReturn("wrongEmail@gmail.com", "incorrectpassword")
        .thenThrow(new RuntimeException("Simulated exit after failed login"));

    assertThrows(RuntimeException.class, () -> userController.login());
  }



  @Test
  void testLogin_nullEmailInput() {
    when(mockTextUserInterface.getUserInput(anyString()))
        .thenReturn(null)
        .thenThrow(new RuntimeException("Simulated exit after null input"));

    assertThrows(RuntimeException.class, () -> userController.login());
  }


  @Test
  void testLogin_multipleUsersOnSystem() {
    String hashedPassword1 = passwordService.hashPlainTextPassword("password1");
    String hashedPassword2 = passwordService.hashPlainTextPassword("password2");
    String hashedPassword3 = passwordService.hashPlainTextPassword("password3");

    userStorage.addUserToMap("user1@uni.ac.uk", new FacultyMember("user1@uni.ac.uk", hashedPassword1));
    userStorage.addUserToMap("user2@uni.ac.uk", new FacultyMember("user2@uni.ac.uk", hashedPassword2));
    userStorage.addUserToMap("user3@uni.ac.uk", new FacultyMember("user3@uni.ac.uk", hashedPassword3));


    when(mockTextUserInterface.getUserInput(anyString())).
        thenReturn("user2@uni.ac.uk", "password2");

    User result = userController.login();

    assertNotNull(result, "Correct user should be returned when multiple users are"
        + " on the system");
  }

  @Test
  void testLogin_facultyMemberWrongPassword() {
    when(mockTextUserInterface.getUserInput(anyString()))
        .thenReturn("johndoe@gmail.com", "wrongpassword")
        .thenThrow(new RuntimeException("Simulated exit after failed login"));

    assertThrows(RuntimeException.class, () -> userController.login());
  }
}
