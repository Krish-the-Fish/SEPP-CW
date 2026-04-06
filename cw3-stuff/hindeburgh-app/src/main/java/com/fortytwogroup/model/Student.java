package com.fortytwogroup.model;

import java.util.Collection;
import java.util.ArrayList;

public class Student extends User {
  private String name;
  private int phoneNumber;
  private StudentPreferences preferences;
  private Collection<Booking> studentBookings;

  public Student(
        String name,
        int phoneNumber,
        String email,
        String password) {
    super(email, password);
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.preferences = new StudentPreferences();
    this.studentBookings = new ArrayList<>();
  }

  public StudentPreferences getPreferenceClass() {
    return preferences;
  }

  public void addBooking(Booking booking) {
    studentBookings.add(booking);
  }

  // public getter for phone number
  public int getPhoneNumber() {
    return phoneNumber;
  }

  public String getName() {
    return name;
  }


}
