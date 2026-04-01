package com.fortytwogroup.model;

import com.fortytwogroup.model.enums.BookingStatus;

import java.time.LocalDateTime;

public class Booking {
  private long bookingNumber;
  private int numTickets;
  private double amountPaid;
  private LocalDateTime bookingDateTime;
  private BookingStatus status;

  public void cancelByStudent() {

  }

  public void cancelPaymentFailed() {

  }

  public void cancelByProvider() {

  }

  public boolean checkBookedByStudent(String email) {
    return false;
  }

  public String getStudentDetails() {
    return null;
  }

  public String generateBookingRecord() {
    return null;
  }
}
