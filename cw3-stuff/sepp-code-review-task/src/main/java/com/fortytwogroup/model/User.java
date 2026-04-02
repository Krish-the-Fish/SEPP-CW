package com.fortytwogroup.model;

import com.fortytwogroup.service.PasswordService;

/**
 * Abstract model class for defining core methods in user objects.
 * Also handles encapsulation of password holding to help with security.
 */
public abstract class User {


  private final String email;
  // don't make password final in case user forgets it
  private String password;  // password will be stored as encrypted version


  /**
   * Constructor for User class. Only intended to be inherited as super constructor.
   * Should not be used on its own
   * @param email String containing the email address of the new user
   * @param password String containing the hashed password of the new user
   */
  public User(String email, String password) {
    this.email = email;
    this.password = password;
  }

  /**
   * Checks if a given plain text password corresponds to the hashed one in the user object
   * @param inputPassword plain text String to be checked if matches hashed password String
   * in object
   * @param passwordService object for handling hashing in the system
   * @return true if the plaintext password, when hashed, matches the user instance's
   * hashed password, otherwise return false
   */
  public boolean checkPasswordMatch(String inputPassword, PasswordService passwordService) {
    if (this.password != null && inputPassword != null) {
      // argon will crash if password null
      return passwordService.checkPasswordMatch(this.password, inputPassword);
    }
    else{
      // if null password, no password match
      return false;
    }
  }


}
