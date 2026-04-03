package com.fortytwogroup.controller;

import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.AdminStaff;
import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.model.User;
import com.fortytwogroup.view.TextUserInterface;

import java.util.HashMap;
import java.util.Map;

public class UserController extends Controller {
  // for later testing, to instantiate the user classes so that we can log in
  private final String REGISTERED_USERS_FILE = "";
  private final String REGISTERED_ADMINS_FILE = "";

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
  }

  public void login() {
    if (!checkCurrentUserIsGuest()) {
      textUserInterface.displayError("You are already logged in!");
    }

    String email = textUserInterface.getInput("Email: ");
    String password = textUserInterface.getInput("Password: ");

    if (!users.containsKey(email)) {
      textUserInterface.displayError("User not found!");
      return;
    }

    User targetUser = users.get(email);

    if (password.equals(targetUser.getPassword())) {
      textUserInterface.displaySuccess("Successfully logged in!");
      setCurrentUser(targetUser);
    }
    else {
      textUserInterface.displayError("Incorrect password!");
      return;
    }
  }

  public void logout() {
    setCurrentUser(null);
    textUserInterface.displaySuccess("Successfully logged out!");
  }

  public void registerEntertainmentProvider() {
    String orgName = textUserInterface.getInput("Organisation Name: ");
    String businessNumber = textUserInterface.getInput("Business Number: ");
    String email = textUserInterface.getInput("Email: ");

    if (EPAccountAlreadyExists(orgName, businessNumber, email)) {
      textUserInterface.displayError("This EP is already registered!");
      return;
    }

    if (!verificationService.verifyEntertainmentProvider(businessNumber)) {
      textUserInterface.displayError("Verification failed!");
      return;
    }

    String password = textUserInterface.getInput("Password: ");
    String name = textUserInterface.getInput("Name: ");
    String description = textUserInterface.getInput("Description: ");

    EntertainmentProvider newEP = new EntertainmentProvider(
            orgName,
            businessNumber,
            name,
            description,
            email,
            password);

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
    if (checkCurrentUserIsStudent()) {
      String preferences = textUserInterface.getInput("Preferences: ");
      ((Student)getCurrentUser()).getPreferenceClass().updatePreferences(preferences);
    }
  }

  private void addUser(User user) {
    String email = user.getEmail();
    users.put(email, user);
  }

  private void addPreregisteredUsers() {

  }

  private EntertainmentProvider getEntertainmentProviderOwningEvent(long eventNumber) {
    return null;
  }



}
