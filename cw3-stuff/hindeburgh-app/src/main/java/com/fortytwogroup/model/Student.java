package com.fortytwogroup.model;

public class Student extends User {
  private String name;
  private int phoneNumber;
  public StudentPreferences preferences;

  public Student(
          String name,
          int phoneNumber,
          String email,
          String password) {
    super(email, password);
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.preferences = new StudentPreferences();
  }

  public void addBooking(Booking booking) {

  }
}
