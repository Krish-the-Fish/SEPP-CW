package com.fortytwogroup.model;

/**
 * Model class for special user type: Faculty member.
 */
public class FacultyMember extends User {

  private int loginAttempts;

  /**
   * Constructor for FacultyMember class
   * @param emailAddress String containing email address of new faculty member instance
   * @param hashedPassword String containing the hashed password of the new
   * faculty member instance
   */
  public FacultyMember(
      String emailAddress,
      String hashedPassword) {
    super(emailAddress, hashedPassword);
    this.loginAttempts = 0;


  }

  /**
   * Getter for loginAttempts variable
   * @return login attempts variable for a given user object
   */
  public int getLoginAttempts() {
    return loginAttempts; }


  /**
   * Adds one to the login attempts variable belonging to a Faculty member instance
   */
  public void incrementLoginAttempts() {
    this.loginAttempts++; }
}
