package com.fortytwogroup.controller;

import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.User;
import com.fortytwogroup.view.TextUserInterface;

public class UserController extends Controller {
  // for later testing, to instantiate the user classes so that we can log in
  private final String REGISTERED_USERS_FILE = "";

  private final TextUserInterface UI;
  //Probably not going to use
  private User user;
  public UserController(TextUserInterface UI) {
    this.UI = UI;
  }

  public void login() {
    if (!checkCurrentUserIsGuest()) {
      UI.displayError("You are already logged in!");
    }

    String userType = UI.getInput("User Type (Student, Admin, EP): ");
    String email = UI.getInput("Email: ");
    String password = UI.getInput("Password: ");

    // Search list of users to try and log in
  }

  public void logout() {

  }

  public void registerEntertainmentProvider() {
    String orgName = UI.getInput("Organisation Name: ");
    String businessNumber = UI.getInput("Business Number: ");
    String email = UI.getInput("Email: ");

    if (EPAccountAlreadyExists(orgName, businessNumber, email)) {
      UI.displayError("This EP is already registered!");
      return;
    }

    String password = UI.getInput("Password: ");
    String name = UI.getInput("Name: ");
    String description = UI.getInput("Description: ");

    EntertainmentProvider newEP = new EntertainmentProvider(
            orgName,
            businessNumber,
            name,
            description,
            email,
            password);
  }

  private boolean EPAccountAlreadyExists(
    String email,
    String orgName,
    String businessNumber) {
    // look through list of registered EPs
    return false;
  }

  public void editPreferences() {
    if (checkCurrentUserIsStudent()) {
      String preferences = UI.getInput("Preferences: ");
      //SET PREFERENCES
    }
  }

  private void addUser(User user) {

  }

  private void addPreregisteredUsers() {

  }

  private EntertainmentProvider getEntertainmentProviderOwningEvent(long eventNumber) {
    return null;
  }



}
