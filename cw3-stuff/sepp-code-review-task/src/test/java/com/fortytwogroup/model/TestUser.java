package com.fortytwogroup.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fortytwogroup.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUser {

  private PasswordService passwordService;
  private String dummyPassword;
  private String dummyHashedPassword;
  private User user;
  private String dummyEmail;

  @BeforeEach
  void setUp() {
    passwordService = new PasswordService();
    dummyPassword = "password123";
    dummyHashedPassword = passwordService.hashPlainTextPassword(dummyPassword);

    user = new User("johndoe@gmail.com", dummyHashedPassword) {
      // anonymous subclass to instantiate abstract User
    };


  }


  @Test
  void testEqualsHashedPassword() {
    assertTrue(user.checkPasswordMatch(dummyPassword, passwordService),
        "Correct password should match its hash");
  }

  @Test
  void testCheckPasswordMatch_nullPassword() {
    assertFalse(user.checkPasswordMatch(null, passwordService),
        "Null password should return false");
  }

  @Test
  void testCheckPasswordMatch_wrongPassword() {
    assertFalse(user.checkPasswordMatch("wrongpassword", passwordService),
        "Wrong password should return false");
  }
}
