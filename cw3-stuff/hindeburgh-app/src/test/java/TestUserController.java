
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 2. JUnit 5 Annotations
import com.fortytwogroup.model.AdminStaff;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.model.StudentPreferences;
import com.fortytwogroup.model.User;
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
    when(textUserInterface.getInput("Email: ")).thenReturn("", "kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("12345", "12345");

    userController.login();

    verify(textUserInterface).displayError("Email cannot be blank!");

    verify(textUserInterface).displaySuccess("Successfully logged in!");

    assertEquals(
            userController.getUsers().get("kevinking@school.com"),
            userController.getCurrentUser(),
            "After failing login due to empty email, a successful login should be allowed"
    );
  }

  @Test
  void login_empty_password() {
    when(textUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com", "kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("", "12345");

    userController.login();

    verify(textUserInterface).displayError("Password cannot be blank!");

    verify(textUserInterface).displaySuccess("Successfully logged in!");

    assertEquals(
            userController.getUsers().get("kevinking@school.com"),
            userController.getCurrentUser(),
            "After failing login due to empty password, a successful login should be allowed"
    );
  }

  @Test
  void login_non_existing_email() {
    when(textUserInterface.getInput("Email: ")).thenReturn("nonexisting@school.com", "kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("", "12345");

    userController.login();

    verify(textUserInterface).displayError("User not found!");

    verify(textUserInterface).displaySuccess("Successfully logged in!");

    assertEquals(
            userController.getUsers().get("kevinking@school.com"),
            userController.getCurrentUser(),
            "After failing login due to empty password, a successful login should be allowed"
    );
  }

  @Test
  void login_incorrect_password() {
    when(textUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com", "kevinking@school.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("incorrectpassword", "12345");

    userController.login();

    verify(textUserInterface).displayError("Incorrect password!");

    verify(textUserInterface).displaySuccess("Successfully logged in!");

    assertEquals(
            userController.getUsers().get("kevinking@school.com"),
            userController.getCurrentUser(),
            "After failing login due to empty password, a successful login should be allowed"
    );
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
    //Staggering the blank values to check error is thrown for all empty fields
    when(textUserInterface.getInput("Organisation Name: ")).thenReturn(
            "", "Eventful Events"
    );
    when(textUserInterface.getInput("Business Number: ")).thenReturn(
            "6372646378", "", "6372646378"
    );
    when(textUserInterface.getInput("Email: ")).thenReturn(
            "eevents@business.com", "eevents@business.com", "", "eevents@business.com"
    );
    when(textUserInterface.getInput("Password: ")).thenReturn(
            "Events123", "Events123", "Events123", "", "Events123"
    );
    when(textUserInterface.getInput("Name: ")).thenReturn(
            "Evan Evans", "Evan Evans", "Evan Evans", "Evan Evans", "", "Evan Evans"
    );
    when(textUserInterface.getInput("Description: ")).thenReturn(
            "disc", "disc", "disc", "disc", "disc", "", "disc"
    );

    userController.registerEntertainmentProvider();

    verify(textUserInterface, times(6)).displayError("A field cannot be blank!");

    verify(textUserInterface).displaySuccess("Successfully registered new EP!");

    assertNotNull(
            userController.getUsers().get("eevents@business.com"),
            "After successfully registering an EP, it should be stored in the map of users"
    );
  }

  @Test
  void registerEntertainmentProvider_invalid_businessNumber() {
    when(textUserInterface.getInput("Organisation Name: ")).thenReturn("Eventful Events", "Eventful Events");
    when(textUserInterface.getInput("Business Number: ")).thenReturn("1", "6372646378");
    when(textUserInterface.getInput("Email: ")).thenReturn("eventfulEvents@business.com", "eventfulEvents@business.com");
    when(textUserInterface.getInput("Password: ")).thenReturn("Events123", "Events123");
    when(textUserInterface.getInput("Name: ")).thenReturn("Evan Evans", "Evan Evans");
    when(textUserInterface.getInput("Description: ")).thenReturn(
            "Disc", "Disc"
    );

    userController.registerEntertainmentProvider();

    verify(textUserInterface).displayError("Verification failed!");

    verify(textUserInterface).displaySuccess("Successfully registered new EP!");

    assertNotNull(
            userController.getUsers().get("eventfulEvents@business.com"),
            "After successfully registering an EP, it should be stored in the map of users"
    );
  }

  @Test
  void registerEntertainmentProvider_ep_already_registered() {
    when(textUserInterface.getInput("Organisation Name: ")).thenReturn(
            "Eventful Events", "Eventful Events", "Popping Parties"
    );
    when(textUserInterface.getInput("Business Number: ")).thenReturn(
            "6372646378", "6372646378", "7483948914"
    );
    when(textUserInterface.getInput("Email: ")).thenReturn(
            "eventfulEvents@business.com", "eventfulEvents@business.com", "pparties@business.com"
    );
    when(textUserInterface.getInput("Password: ")).thenReturn(
            "Events123", "Events123", "parties"
    );
    when(textUserInterface.getInput("Name: ")).thenReturn(
            "Evan Evans", "Evan Evans", "Paul Porter"
    );
    when(textUserInterface.getInput("Description: ")).thenReturn(
            "Desc"
    );

    userController.registerEntertainmentProvider();
    userController.registerEntertainmentProvider();

    verify(textUserInterface).displayError("This EP is already registered!");
  }

  @Test
  void editPreferences_successful() {
    Student mockStudent = mock(Student.class);
    StudentPreferences mockStudentPreferences = mock(StudentPreferences.class);

    when(textUserInterface.getInput("Preferences: ")).thenReturn("music, dance, theatre");

    when(mockStudent.getPreferenceClass()).thenReturn(mockStudentPreferences);

    when(mockStudentPreferences.updatePreferences("music, dance, theatre")).thenReturn(true);

    userController.setCurrentUser(mockStudent);

    userController.editPreferences();

    verify(mockStudentPreferences).updatePreferences("music, dance, theatre");

    verify(textUserInterface).displaySuccess("Preferences updated!");

    verify(textUserInterface, never()).displayError(anyString());
  }

  @Test
  void editPreferences_invalid_preferences() {
    Student mockStudent = mock(Student.class);
    StudentPreferences mockStudentPreferences = mock(StudentPreferences.class);

    when(textUserInterface.getInput("Preferences: ")).thenReturn(
            "music, dance, INVALID", "music, dance, theatre"
    );

    when(mockStudent.getPreferenceClass()).thenReturn(mockStudentPreferences);

    when(mockStudentPreferences.updatePreferences("music, dance, theatre")).thenReturn(true);

    userController.setCurrentUser(mockStudent);

    userController.editPreferences();

    verify(textUserInterface).displayError("INVALID is not a valid preference!");

    verify(mockStudentPreferences).updatePreferences("music, dance, theatre");

    verify(textUserInterface).displaySuccess("Preferences updated!");
  }

  @Test
  void editPreferences_multiple_invalids() {
    Student mockStudent = mock(Student.class);
    StudentPreferences mockStudentPreferences = mock(StudentPreferences.class);

    when(textUserInterface.getInput("Preferences: ")).thenReturn(
            "INVALID, INVALID, INVALID", "music, dance, theatre"
    );

    when(mockStudent.getPreferenceClass()).thenReturn(mockStudentPreferences);

    when(mockStudentPreferences.updatePreferences("music, dance, theatre")).thenReturn(true);

    userController.setCurrentUser(mockStudent);

    userController.editPreferences();

    verify(textUserInterface, times(3)).displayError("INVALID is not a valid preference!");

    verify(mockStudentPreferences).updatePreferences("music, dance, theatre");

    verify(textUserInterface).displaySuccess("Preferences updated!");
  }

  @Test
  void editPreferences_too_many_selections() {
    Student mockStudent = mock(Student.class);
    StudentPreferences mockStudentPreferences = mock(StudentPreferences.class);

    when(textUserInterface.getInput("Preferences: ")).thenReturn(
            "music, dance, theatre, sports", "music, dance, theatre"
    );

    when(mockStudent.getPreferenceClass()).thenReturn(mockStudentPreferences);

    when(mockStudentPreferences.updatePreferences("music, dance, theatre")).thenReturn(true);

    userController.setCurrentUser(mockStudent);

    userController.editPreferences();

    verify(textUserInterface).displayError("Too many preferences!");

    verify(mockStudentPreferences).updatePreferences("music, dance, theatre");

    verify(textUserInterface).displaySuccess("Preferences updated!");
  }
}
