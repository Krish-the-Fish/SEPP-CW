package com.fortytwogroup.controller;

import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.*;
import com.fortytwogroup.model.enums.EventType;
import com.fortytwogroup.view.TextUserInterface;

import java.io.File;
import java.util.*;

/**
 * Class controlling basic user functionality, allowing users to login, logout and edit specific
 * details about their account such as their preferences.
 */
public class UserController extends Controller {
  // for later testing, to instantiate the user classes so that we can log in
  private final String REGISTERED_ADMINS_FILE = "src/main/resources/pre-registered-admins.csv";
  private final String REGISTERED_USERS_FILE = "src/main/resources/pre-registered-users.csv";

  private Map<String, User> users;

  private final TextUserInterface textUserInterface;
  private final MockVerificationService verificationService;

  public UserController(
          TextUserInterface textUserInterface,
          MockVerificationService verificationService) {
    this.textUserInterface = textUserInterface;
    this.verificationService = verificationService;
    setCurrentUser(null);
    users = new HashMap<String, User>();

    addPreregisteredUsers();
  }

  public Map<String, User> getUsers() {
    return users;
  }

  /**
   * Allows users to log in to their account on the system.
   * Handles input validation, allowing users to re-enter details on a failed log in attempt
   */
  public void login() {
    boolean validLogin = false;
    while (!validLogin) {
      validLogin = true;

      String email = textUserInterface.getInput("Email: ");
      String password = textUserInterface.getInput("Password: ");

      if (email.isBlank()) {
        textUserInterface.displayError("Email cannot be blank!");
        validLogin = false;
      }

      if (password.isBlank()) {
        textUserInterface.displayError("Password cannot be blank!");
        validLogin = false;
      }

      if (!users.containsKey(email)) {
        textUserInterface.displayError("User not found!");
        validLogin = false;
        continue;
      }

      User targetUser = users.get(email);
      if (password.equals(targetUser.getPassword())) {
        textUserInterface.displaySuccess("Successfully logged in!");
        setCurrentUser(targetUser);
      }
      else {
        textUserInterface.displayError("Incorrect password!");
        validLogin = false;
      }
    }
  }

  /**
   * Allows users to log out of the system.
   * Represents this by setting the state of the current user on the thread to null
   */
  public void logout() {
    setCurrentUser(null);
    textUserInterface.displaySuccess("Successfully logged out!");
  }

  public void registerEntertainmentProvider() {
    String orgName = "";
    String businessNumber = "";
    String email = "";
    String password = "";
    String name = "";
    String description = "";

    boolean validRegistration = false;
    while (!validRegistration) {
      validRegistration = true;

      orgName = textUserInterface.getInput("Organisation Name: ");
      businessNumber = textUserInterface.getInput("Business Number: ");
      email = textUserInterface.getInput("Email: ");
      password = textUserInterface.getInput("Password: ");
      name = textUserInterface.getInput("Name: ");
      description = textUserInterface.getInput("Description: ");

      boolean emptyFields = false;
      for (String input : new String[] { orgName, businessNumber, email, password, name, description }) {
        if (input.isBlank()) {
          emptyFields = true;
          break;
        }
      }

      if (emptyFields) {
        textUserInterface.displayError("A field cannot be blank!");
        validRegistration = false;
        continue;
      }

      if (EPAccountAlreadyExists(email, orgName, businessNumber)) {
        textUserInterface.displayError("This EP is already registered!");
        validRegistration = false;
        continue;
      }

      if (!verificationService.verifyEntertainmentProvider(businessNumber)) {
        textUserInterface.displayError("Verification failed!");
        validRegistration = false;
      }
    }

    EntertainmentProvider newEP = new EntertainmentProvider(
            orgName,
            businessNumber,
            name,
            description,
            email,
            password);

    textUserInterface.displaySuccess("Successfully registered new EP!");

    users.put(email, newEP);
  }

  private boolean EPAccountAlreadyExists(
    String email,
    String orgName,
    String businessNumber) {

    // Businesses cannot have the same orgname as a preregistered business or the same business number
    boolean inHashMap = users.containsKey(email) && users.get(email) instanceof EntertainmentProvider;

    boolean found = false;
    for (User user : users.values()) {
      if (user instanceof EntertainmentProvider &&
          (((EntertainmentProvider) user).getBusinessNumber().equals(businessNumber) ||
            ((EntertainmentProvider) user).getOrgName().equals(orgName))) {
          found = true;
      }
    }

    return found || inHashMap;
  }

  /**
   * Allows students to edit their preferences by allowing them to say if they're interested
   * in particular types of events.
   * Preferences affect the rankings of search results when student's search for performances
   */
  public void editPreferences() {
    List<String> eventTypes = getEventTypes();
    String preferences = "";

    boolean validInput = false;
    while (!validInput) {
      validInput = true;
      System.out.println("Choose up to 3 preferences out of " + String.join(", ", eventTypes));
      System.out.println("Write your choices in the same line with one comma in between");
      preferences = textUserInterface.getInput("Preferences: ");
      String[] preferencesArray = preferences.split("\\s*,\\s*");

      int counter = 0;
      for (String preference : preferencesArray) {
        counter++;
        if (!eventTypes.contains(preference.toUpperCase())) {
          validInput = false;
          textUserInterface.displayError(preference + " is not a valid preference!");
        }
      }

      if (counter > 3) {
        textUserInterface.displayError("Too many preferences!");
        validInput = false;
      }
    }

    boolean out = ((Student)getCurrentUser()).getPreferenceClass().updatePreferences(preferences);

    if (out) {
      textUserInterface.displaySuccess("Preferences updated!");
    }
    else {
      textUserInterface.displayError("Something went wrong...");
    }
  }

  private List<String> getEventTypes() {
    List<String> eventTypes = new ArrayList<String>();

    for (EventType eventType : EventType.values()) {
      eventTypes.add(eventType.toString());
    }

    return eventTypes;
  }

  /**
   * Adds a new user to the system once they've registered for an account
   * @param user the object representing the new user just added to the system
   */
  private void addUser(User user) {
    String email = user.getEmail();
    users.put(email, user);
  }


  /**
   * Instantiates a list of user objects representing all of pre-registered defined by Acme Corp
   */
  private void addPreregisteredUsers() {
    File userFile = new File(REGISTERED_USERS_FILE);
    File adminFile = new File(REGISTERED_ADMINS_FILE);

    readFromFile(userFile, true);
    readFromFile(adminFile, false);
  }

  private void readFromFile(File file, boolean student) {
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.isBlank()) continue;

        String[] data = line.split("\\s*,\\s*");
        String email = data[0];
        String password = data[1];
        String name = data[2];
        if (student) {
          int phoneNumber = Integer.parseInt(data[3]);
          Student newStudent = new Student(name, phoneNumber, email, password);
          addUser(newStudent);
        }
        else {
          AdminStaff admin = new AdminStaff(name, email, password);
          addUser(admin);
        }
      }
    }
    catch (Exception e) {
      System.out.println("File Error: " + e.getMessage() + " in " + file.getAbsolutePath());
    }
  }

  private EntertainmentProvider getEntertainmentProviderOwningEvent(long eventNumber) {
    for (User user : users.values()) {
      if (user instanceof EntertainmentProvider) {
        Collection<Event> events = ((EntertainmentProvider) user).getEvents();
        for (Event event : events) {
          if (event.getId() == eventNumber) {
            return (EntertainmentProvider) user;
          }
        }
      }
    }
    return null;
  }
}
