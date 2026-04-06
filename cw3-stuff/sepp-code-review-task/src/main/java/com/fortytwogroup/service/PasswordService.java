package com.fortytwogroup.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Implements external hashing system for password hashing, allowing for secure password
 * storage on the system by storing passwords as hashes instead of plain text
 */
public class PasswordService {
  // int argon password factory object
  /**
   * Argon2id instance used for password hashing and verification
   */
  private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

  /**
   * Turns a plaintext password stored as type String into a salted hash
   * @param password String containing password to be turned into its hash
   * @return input password String as hash String
   */
  public String hashPlainTextPassword(String password) {
    return argon2.hash(10, 65536, 4, password.toCharArray());
  }

  // input will be as plain text
  // passwords in system are encrypted text

  /**
   * Calls argon package for checking if a plain text password corresponds to a hashed one
   * @param hash hashed password that we're checking equality against
   * @param queryPassword plain text password we want to see if matches the hash when
   * hashed itself
   * @return true if plain text password corresponds to hash, otherwise return false
   */
  public boolean checkPasswordMatch(
      String hash,
      String queryPassword) {

    return argon2.verify(hash, queryPassword.toCharArray());
  }
}
