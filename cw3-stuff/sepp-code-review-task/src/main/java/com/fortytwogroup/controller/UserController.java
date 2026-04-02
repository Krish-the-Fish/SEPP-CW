package com.fortytwogroup.controller;

import com.fortytwogroup.model.FacultyMember;
import com.fortytwogroup.model.User;
import com.fortytwogroup.model.UserStorage;
import com.fortytwogroup.service.RegistrationUtility;
import com.fortytwogroup.view.TextUserInterface;
import com.fortytwogroup.service.PasswordService;

/**
 * Controller class for managing user authentication and login logic.
 * Handles both existing users and first-time faculty member logins
 * via lazy migration.
 */
public class UserController {

  private final TextUserInterface textUserInterface;
  private final UserStorage userStorage;
  private final PasswordService passwordService;
  private final RegistrationUtility registrationUtility;


  // dependency injection for including system objects

  /**
   * Constructor for UserController class
   * @param userStorage UserStorage instance containing all user objects on the system
   * @param textUserInterface TextUserInterface instance for accepting user input, prompting
   * the user, and returning success/error messages
   * @param passwordService PasswordService instance for dealing with password hashing
   * @param registrationUtility RegistrationUtility instance for checking if emails belong
   * to faculty members logging in for the first time
   */
  public UserController(
      UserStorage userStorage,
      TextUserInterface textUserInterface,
      PasswordService passwordService,
      RegistrationUtility registrationUtility) {

    this.userStorage = userStorage;
    this.textUserInterface = textUserInterface;
    this.passwordService = passwordService;
    this.registrationUtility = registrationUtility;
  }

  /**
   * Verifies that a user is on the system and returns user object to represent logged in state.
   * If user not on system but on faculty member list and first time logging in, make an
   * account for them in the form of a FacultyMember object and return that object to show their
   * state as logged in.
   * @return returns User object if log in successful, otherwise returns null
   */
  public User login() {

    // call view to get email input
    String inputEmail = textUserInterface.getEmailInput();
    inputEmail = inputEmail.trim();


    // call view to get  password
    String inputPassword = textUserInterface.getPasswordInput();
    // will reject null passwords later on in method

    // check if already an account with the input email on the system
    if (checkIfEmailOnSystem(inputEmail)) {
      // query user storage to check get corresponding user object for email address
      User correspondingUser = userStorage.getUserByEmail(inputEmail);

      if (correspondingUser != null) {
        // check if password match for that user
        boolean isPasswordMatch =
            correspondingUser.checkPasswordMatch(inputPassword, passwordService);

        // increment login attempt if faculty member
        if (correspondingUser instanceof FacultyMember) {
          ((FacultyMember) correspondingUser).incrementLoginAttempts();
        }

        if (isPasswordMatch) {
          // login successful if this is the case
          return correspondingUser;
        }
      }
    }
    else{
      // email not already on system if this else block executes
      // check RegistrationUtility table
      boolean inFacultyList = registrationUtility.verifyInFacultyFile(
          inputEmail,
          inputPassword,
          passwordService);

      if (inFacultyList) {
        // create new faculty user
        FacultyMember facultyMember = registrationUtility.registerFacultyMember(
            inputEmail,
            passwordService.hashPlainTextPassword(inputPassword));

        // add the user to the systems map of registered users
        userStorage.addUserToMap(inputEmail, facultyMember);

        return facultyMember;

      }
    }

    // return null if login failed
    textUserInterface.displayLoginErrorMessage();
    return null;

  }

  /**
   * Checks if a given email address matches one in a User with an object already on the system
   * @param email String containing the email address given by the user
   * @return true if email matches one of a User object currently on the system, otherwise false.
   */
  private boolean checkIfEmailOnSystem(String email) {
    // basic input validation
    if (email == null || email.isEmpty()) {
      return false;
    }
    // now check against hashmap of stored users
    return userStorage.checkIfEmailOnSystem(email);
  }

}
