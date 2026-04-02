package com.fortytwogroup.model;

import com.fortytwogroup.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class TestFacultyMember {

  private FacultyMember facultyMember;
  private PasswordService passwordService;

  @BeforeEach
  void setUp() {
    // runs before every test - fresh objects each time
    passwordService = new PasswordService();
    String hashed = passwordService.hashPlainTextPassword("password123");
    facultyMember = new FacultyMember("john@uni.ac.uk", hashed);
  }

  // Test constructor producing object of the correct type

  @Test
  void testConstructor_isCorrectType() {
    assertInstanceOf(FacultyMember.class, facultyMember,
        "Should be a FacultyMember instance");
  }

  // Test that login attempts are starting at zero
  @Test
  void testLoginAttempts_initialValueZero() {
    assertEquals(0, facultyMember.getLoginAttempts(),
        "Login attempts should be zero at FacultyMember construction");

  }

  // Test that login attempts increment method does so by exactly 1 when invoked
  @Test
  void testIncrementLoginAttempts_onceGivesOne() {
    facultyMember.incrementLoginAttempts();
    assertEquals(1, facultyMember.getLoginAttempts(),
        "Login attempts should be 1 after one increment");
  }

  @Test
  void testIncrementLoginAttempts_twiceGivesTwo() {
    facultyMember.incrementLoginAttempts();
    facultyMember.incrementLoginAttempts();
    assertEquals(2, facultyMember.getLoginAttempts(),
        "Login attempts should be 2 after two increments");
  }


}

