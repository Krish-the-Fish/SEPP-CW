package com.fortytwogroup.model;

import com.fortytwogroup.model.enums.BookingStatus;
import com.fortytwogroup.model.Student;

import com.fortytwogroup.model.enums.PerformanceStatus;
import java.time.LocalDateTime;

public class Booking {
  private long bookingNumber;
  private int numTickets;
  private double amountPaid;
  private LocalDateTime bookingDateTime;
  private BookingStatus status;
  private Student student;  // including a reference to the student who made the booking
  private Performance performance;

  // constructor
  public Booking(
      long bookingNumber,
      int numTickets,
      double amountPaid,
      Student student,
      Performance chosenPerformance) {

    this.bookingNumber = bookingNumber;
    this.numTickets = numTickets;
    this.amountPaid = amountPaid;
    this.student = student;
    this.status = BookingStatus.ACTIVE;  // init status to active upon instantiation
    this.performance = chosenPerformance;
    // can change status if booking is cancelled or payment fails

    // get the time of construction as time of booking
    this.bookingDateTime = LocalDateTime.now();


  }
  public void cancelByStudent() {

  }

  /**
   * Method should only be called by BookingController
   */
  public void cancelPaymentFailed() {
    // update status of booking
    status = BookingStatus.PAYMENT_FAILED;
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
    String bookingRecord = "";
    bookingRecord += "Student name: " + student.getName()+"\n";
    bookingRecord += "Student email: " + student.getEmail()+"\n";
    bookingRecord += "Student phone number" + student.getPhoneNumber()+"\n";
    bookingRecord += performance.toStringSensitive()+"\n";  // possibly don't allow to see sponsorship amount
    bookingRecord += performance.getEvent().toString()+"\n";
    // sequence diagram does not state to include the time the event was booked at

    return bookingRecord;
  }
}
