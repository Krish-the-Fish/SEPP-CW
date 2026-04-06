package com.fortytwogroup.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Central storage class for storing objects for all users registered on the system.
 * Also guards what classes can view certain details of User Objects
 */
public class UserStorage {

  // hashmap should have (email address String, com.fortytwogroup.model.User object) pairs
  private Map<String, User> users;

  /**
   * Constructor for UserStorage class
   */
  public UserStorage() {
    this.users = new HashMap<>();
  }

  /**
   * Returns the user object for a given email address
   * @param email String containing an email address
   * @return User object for input email address
   */
  public User getUserByEmail(String email) {
    return this.users.get(email);
  }

  /**
   * Checks if a given String containing an email address matches one of the keys on the
   * system's internal map of all users
   * @param email String containing email address that is to be checked if the system has
   * @return true if key matches one in system's map of users, otherwise returns false
   */
  public boolean checkIfEmailOnSystem(String email) {

    return this.users.containsKey(email);
  }

  /**
   * Adds an email, user pair to the system's map of all users. Meant for when a new user
   * is added to the system
   * @param email String containing the email address of a new user
   * @param user user object corresponding to the email String
   */
  public void addUserToMap(
      String email,
      User user) {

    users.put(email, user);
  }

}
