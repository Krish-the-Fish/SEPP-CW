package com.fortytwogroup.model;

/**
 * Abstract class not meant to be instantiated.
 * Purely to allow the 3 user types: AdminStaff, Student, EntertainmentProvider, to inherit
 * common attributes and functionality.
 */
public abstract class User {
  private String email;
  private String password;

  public User(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
