
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 2. JUnit 5 Annotations
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.view.TextUserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUserController {

  private UserController userController;
  private TextUserInterface textUserInterface;
  private MockVerificationService mockVerificationService;

  @BeforeEach
  void setUp() {
    mockVerificationService = new MockVerificationService();
    textUserInterface = mock(TextUserInterface.class);
    userController = new UserController(textUserInterface, mockVerificationService);
  }

  @Test
  void login_successful() {
    //kevinking@school.com, 12345, Kevin King
    when(textUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("12345");

    userController.login();

    verify(textUserInterface).displaySuccess("Successfully logged in!");

    assertEquals(
            userController.getUsers().get("kevinking@school.com"),
            userController.getCurrentUser(),
            "Loging in should assign the correct account to the current user variable."
    );
  }

  @Test
  void login_empty_email() {
    when(textUserInterface.getInput("Email: ")).thenReturn("");
    when(textUserInterface.getInput("Password: ")).thenReturn("12345");

    userController.login();

    verify(textUserInterface).displayError("Email cannot be blank!");
  }

  @Test
  void login_empty_password() {
    when(textUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("");

    userController.login();

    verify(textUserInterface).displayError("Password cannot be blank!");
  }

  @Test
  void login_non_existing_email() {
    when(textUserInterface.getInput("Email: ")).thenReturn("nonexisting@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("");

    userController.login();

    verify(textUserInterface).displayError("User not found!");
  }

  @Test
  void login_incorrect_password() {
    when(textUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("incorrectpassword");

    userController.login();

    verify(textUserInterface).displayError("Incorrect password!");
  }

  @Test
  void logout_successful() {
    when(textUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("12345");

    userController.login();
    userController.logout();

    verify(textUserInterface).displaySuccess("Successfully logged out!");

    assertNull(
            userController.getCurrentUser(),
            "Loging out should result in the current user being set to null."
    );
  }

  @Test
  void registerEntertainmentProvider_successful() {
    when(textUserInterface.getInput("Organisation Name: ")).thenReturn("Eventful Events");
    when(textUserInterface.getInput("Business Number: ")).thenReturn("6372646378");
    when(textUserInterface.getInput("Email: ")).thenReturn("eventfulEvents@business.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("Events123");
    when(textUserInterface.getInput("Name: ")).thenReturn("Evan Evans");
    when(textUserInterface.getInput("Description: ")).thenReturn(
            "A large scale event company dedicated to making fun events for students"
    );

    userController.registerEntertainmentProvider();

    verify(textUserInterface).displaySuccess("Successfully registered new EP!");

    assertNotNull(
            userController.getUsers().get("eventfulEvents@business.com"),
            "After successfully registering an EP, it should be stored in the map of users"
    );
  }

  @Test
  void registerEntertainmentProvider_empty_field() {
    when(textUserInterface.getInput("Organisation Name: ")).thenReturn("Eventful Events");
    when(textUserInterface.getInput("Business Number: ")).thenReturn("");
    when(textUserInterface.getInput("Email: ")).thenReturn("eventfulEvents@business.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("Events123");
    when(textUserInterface.getInput("Name: ")).thenReturn("Evan Evans");
    when(textUserInterface.getInput("Description: ")).thenReturn(
            "A large scale event company dedicated to making fun events for students"
    );

    userController.registerEntertainmentProvider();

    verify(textUserInterface).displayError("A field cannot be blank!");

    assertNull(
            userController.getUsers().get("eventfulEvents@business.com"),
            "Failing to register an EP, it should not be stored in the map of users"
    );
  }

  @Test
  void registerEntertainmentProvider_invalid_businessNumber() {
    when(textUserInterface.getInput("Organisation Name: ")).thenReturn("Eventful Events");
    when(textUserInterface.getInput("Business Number: ")).thenReturn("1");
    when(textUserInterface.getInput("Email: ")).thenReturn("eventfulEvents@business.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("Events123");
    when(textUserInterface.getInput("Name: ")).thenReturn("Evan Evans");
    when(textUserInterface.getInput("Description: ")).thenReturn(
            "A large scale event company dedicated to making fun events for students"
    );

    userController.registerEntertainmentProvider();

    verify(textUserInterface).displayError("Verification Failed!");

    assertNull(
            userController.getUsers().get("eventfulEvents@business.com"),
            "Failing to register an EP, it should not be stored in the map of users"
    );
  }

  @Test
  void registerEntertainmentProvider_ep_already_registered() {
    when(textUserInterface.getInput("Organisation Name: ")).thenReturn("Eventful Events");
    when(textUserInterface.getInput("Business Number: ")).thenReturn("6372646378");
    when(textUserInterface.getInput("Email: ")).thenReturn("eventfulEvents@business.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("Events123");
    when(textUserInterface.getInput("Name: ")).thenReturn("Evan Evans");
    when(textUserInterface.getInput("Description: ")).thenReturn(
            "A large scale event company dedicated to making fun events for students"
    );

    userController.registerEntertainmentProvider();

    verify(textUserInterface).displayError("Verification Failed!");
  }


}
