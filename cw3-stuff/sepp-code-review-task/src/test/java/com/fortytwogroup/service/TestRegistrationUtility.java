package com.fortytwogroup.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fortytwogroup.model.FacultyMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestRegistrationUtility {

  private RegistrationUtility registrationUtility;
  private PasswordService passwordService;


  // src/test
  @BeforeEach
  void setUp() {
    passwordService = new PasswordService();
    registrationUtility = new RegistrationUtility("src/test/resources/faculty.csv");
  }

  @Test
  void testVerifyInFacultyFile_validCredentials() {
    assertTrue(registrationUtility.verifyInFacultyFile(
            "johndoe@gmail.com", "12345", passwordService),
        "Valid email and password should return true");
  }

  @Test
  void testVerifyInFacultyFile_wrongPassword() {
    assertFalse(registrationUtility.verifyInFacultyFile(
            "johndoe@gmail.com", "wrongpassword", passwordService),
        "Wrong password should return false");
  }

  @Test
  void testVerifyInFacultyFile_emailNotInFile() {
    assertFalse(registrationUtility.verifyInFacultyFile(
            "nobody@uni.ac.uk", "12345", passwordService),
        "Email not in file should return false");
  }

  @Test
  void testVerifyInFacultyFile_nullPassword() {
    assertFalse(registrationUtility.verifyInFacultyFile(
            "johndoe@gmail.com", null, passwordService),
        "Null password should return false");
  }

  @Test
  void testVerifyInFacultyFile_nullEmail() {
    assertFalse(registrationUtility.verifyInFacultyFile(
            null, "12345", passwordService),
        "Null email should return false");
  }

  @Test
  void testRegisterFacultyMember_returnsCorrectType() {
    FacultyMember member = registrationUtility.registerFacultyMember(
        "johndoe@gmail.com", "hashedPass");
    assertInstanceOf(FacultyMember.class, member,
        "Should return a FacultyMember instance");
  }

  @Test
  void testRegisterFacultyMember_notNull() {
    FacultyMember member = registrationUtility.registerFacultyMember(
        "johndoe@gmail.com", "hashedPass");
    assertNotNull(member, "Returned FacultyMember should not be null");
  }


}
