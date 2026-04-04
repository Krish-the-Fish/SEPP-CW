package com.fortytwogroup.controller;

import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.AdminStaff;
import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.model.User;
import com.fortytwogroup.model.enums.EventType;
import com.fortytwogroup.view.TextUserInterface;

import java.io.File;
import java.util.*;

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

      for (String input : new String[] { orgName, businessNumber, email, password, name, description }) {
        if (input.isBlank()) {
          textUserInterface.displayError("A field cannot be blank!");
          validRegistration = false;
          break;
        }
      }

      if (EPAccountAlreadyExists(orgName, businessNumber, email)) {
        textUserInterface.displayError("This EP is already registered!");
        return;
      }

      if (!verificationService.verifyEntertainmentProvider(businessNumber)) {
        textUserInterface.displayError("Verification failed!");
        return;
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

    // UNUSED PARAMETERS, COULD BE ISSUE OR SOMETHING TO TALK ABOUT IN REVIEW
    return users.containsKey(email) && users.get(email) instanceof EntertainmentProvider;
  }

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

  private void addUser(User user) {
    String email = user.getEmail();
    users.put(email, user);
  }

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
    return null;
  }



}
