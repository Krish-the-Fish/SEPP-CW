package com.fortytwogroup.service;

import com.fortytwogroup.model.FacultyMember;
import com.opencsv.CSVReaderHeaderAware;
import java.io.FileReader;
import java.util.Map;


/**
 * Class for checking if input emails also exist on a given user list containing email addresses
 * and hashed passwords. Each instance has its own user list.
 */
public class RegistrationUtility {

  private final String filePath;

  // method to call constructor within Faculty member class

  /**
   * Calls constructor from FacultyMember class
   * @param email String containing the user's email address
   * @param hashedPassword String containing password hashed by Password service implementation
   * @return new FacultyMember object
   */
  public FacultyMember registerFacultyMember(
      String email,
      String hashedPassword) {

    return new FacultyMember(email, hashedPassword);
  }

  /**
   * Constructor for RegistrationUtility class
   * @param filePath raw or relative file path of file containing faculty member details
   *
   */
  public RegistrationUtility(String filePath) {
    this.filePath = filePath;
  }

  /**
   * Method for getting the corresponding ground truth hashed password for a faculty member
   * who has never logged in before
   * @param emailAddress String containing the email address in login form
   * @return password for corresponding email address if in file, otherwise null if email
   * address not in file
   */
  private String checkFacultyFileForEmailPassword(String emailAddress) {
    // using while loop inside this method for possible early termination and no caching
    try (CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader(filePath))) {
      Map<String, String> row;
      while ((row = reader.readMap()) != null) {
        if (row.get("email").equals(emailAddress)) {
          // password returned here is exactly as is in the file
          return row.get("password");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return null;

  }


  // password should be plain text here
  // only want one password service instance per user, will define this behaviour in main

  /**
   * Verifies if a plain text input password when hashed matches the one in the hashed one
   * for the corresponding email address
   * @param emailAddress raw email entered by user when prompted
   * @param password non-hashed password entered by user (raw string from user input)
   * @param passwordService Password service object for managing hashing and hashing
   * equality checking
   * @return true if input password matches one in file when hashed, otherwise return false
   */
  public boolean verifyInFacultyFile(
      String emailAddress,
      String password,
      PasswordService passwordService) {

    String actualPassword = checkFacultyFileForEmailPassword(emailAddress);

    if (actualPassword != null && password != null) {
      return passwordService.checkPasswordMatch(actualPassword, password);
    }

    // either csv had a blank entry, didn't have email or input password was null
    return false;
  }


}
