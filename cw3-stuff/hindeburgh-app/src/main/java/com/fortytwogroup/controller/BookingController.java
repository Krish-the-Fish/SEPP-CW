package com.fortytwogroup.controller;

import com.fortytwogroup.model.Booking;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.view.View;
import external.PaymentSystem;
import com.fortytwogroup.model.enums.BookingStatus;
import com.fortytwogroup.model.enums.PerformanceStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;


public class BookingController extends Controller {

  private long nextBookingNumber;

  //shared with EventPerformanceController
  private long Collection<Performance> performances;

  private long View view;
  private long PaymentSystem paymentSystem;

  public BookingController(Collection<Performance> performances, View view, PaymentSystem paymentSystem) {
    this.nextBookingNumber = 1;
    this.performances = performances;
    this.view = view;
    this.paymentSystem = paymentSystem;

  }

  public void bookPerformance() {

    //checks if user is a student
    if (!checkCurrentUserIsStudent()) {
      view.displayError("Only students can book performances");
      return;

  }

    Performance performance = null;
    boolean possible = false;
    boolean isTicketed = true;
    int numTicketsRequested = 0

    //loop: while performance == null or (possible == false and isTicketed == true)
    while (performance == null || (!possible && isTicketed)) {

      //get booking info from the user (performanceID and numTicketsRequested)
      String performanceIDStr = view.getInput("Enter performance ID: ");
      long performanceID;

      try {
        performanceID = Long.parseLong(performanceIDStr);

      } catch (NumberFormatException e) {
        view.displayError("Invalid performance ID");
        continue;

      }

      String numTicketsStr = view.getInput("Enter number of tickets: ");

      try {
        numTicketsRequested = Integer.parseInt(numTicketsStr);

      } catch (NumberFormatException e) {
        view.displayError("Invalid number of tickets");
        continue;

      }

      //check performance by ID
      performance = getPerformanceByID(performanceID);

      if (performance == null) {

        //performance ID is incorrect
        view.displayError("Performance with the entered number does not exist");
        continue;

      }

      //check if booking is possible (If ticketed + enough tickets)
      possible = checkIfBookingPossible(performance, numTicketsRequested);
      isTicketed = performance.checkIfEventIsTicketed();

      //if not possible but loop will continue, performance may need to be reset
      if (!possible && !isTicketed) {

        //non-ticketed event - stop loop
        return;

      }

    }

    //get the current student
    Student student = (Student) getCurrentUser();

    //calculate transaction amount
    double ticketPrice = performance.getFinalTicketPrice();
    double transactionAmount = ticketPrice * numTicketsRequested;

    //create the booking
    Booking booking = new Booking(nextBookingNumber, numTicketsRequested, transactionAmount, student, performance);
    nextBookingNumber++;

    //add booking to performance and student
    addBooking(booking);

    //get info needed for payment
    String eventTitle = performance.getEventTitle();
    String studentEmail = student.getEmail();
    int studentPhone = student.getPhoneNumber();
    String epEmail = performance.getOrganiserEmail();

    //process payment through the external payment system
    boolean paymentSuccessful = paymentSystem.processPayment(numTicketsRequested, eventTitle, studentEmail, studentPhone, epEmail, transactionAmount);

    if (!paymentSuccessful) {

      //payment failed
      view.displayError("There was an issue with payment");
      booking.cancelPaymentFailed();
      return;

    }

    //payment successful - update the number of tickets sold
    int numTicketsSold = performance.getNumTicketsSold();
    performance.setNumTicketsSold(numTicketsSold + numTicketsRequested);

    view.displaySuccess("Booking Successful");

    //generate and display booking record
    String bookingRecord = booking.generateBookingRecord();
    view.displayBookingRecord(bookingRecord);

  }




  public void reviewPerformance() {

  }

  public void cancelBooking() {

  }

  private void addBooking(Booking b) {

  }

  private Performance getPerformanceByID(long performanceID) {
    return null;
  }

  private boolean checkIfBookingPossible(Performance performance, int numTickets) {
    return false;
  }

  private Collection<Booking> findBookingsByEventID(long eventID) {
    return null;
  }

  private Booking getBookingByNumber(long bookingNumber) {
    return null;
  }
}
