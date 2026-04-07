package com.fortytwogroup.model;

/**
 * Class representing the Pre-registered university administrators on the system
 */
public class AdminStaff extends User {
  private String name;

  public AdminStaff(
          String name,
          String email,
          String password) {
    super(email, password);
    this.name = name;
  }
}
