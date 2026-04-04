package com.fortytwogroup.controller;

import com.fortytwogroup.model.Booking;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.view.View;
import com.fortytwogroup.external.PaymentSystem;

import java.util.ArrayList;
import java.util.Collection;


public class BookingController extends Controller {

  private long nextBookingNumber;

  // shared with EventPerformanceController
  private Collection<Performance> performances;

  // collection of all bookings in the system
  private Collection<Booking> allBookingsInSystem;

  private final View view;
  private final PaymentSystem paymentSystem;

  // performances collection should come from MenuController
  public BookingController(
      Collection<Performance> performances,
      View view,
      PaymentSystem paymentSystem) {
    this.nextBookingNumber = 1;  // first booking number should be 1
    this.performances = performances;  //
    this.view = view;
    this.paymentSystem = paymentSystem;
    this.allBookingsInSystem = new ArrayList<>();

  }

  public void bookPerformance() {

    //checks if user is a student
    if (!checkCurrentUserIsStudent()) {
      view.displayError("Only students can book performances");
      return;
    }

    String enterPerformanceIDPrompt = "Enter Performance ID ";
    String enterNumTicketsRequestedPrompt = "Enter Number of Tickets ";

    boolean possible = false;
    Performance chosenPerformance = null;
    long chosenPerformanceID = -1;
    int chosenNumTickets = -1;

    //loop: while performance == null or (possible == false and isTicketed == true)
    // chosenPerformance.checkIfEventIsTicketed condition on while should be redundant now
    while(chosenPerformance == null || (!possible && chosenPerformance.checkIfEventIsTicketed())) {

      String chosenPerformanceIDRawString = view.getInput(enterPerformanceIDPrompt);
      String chosenNumTicketsRawString = view.getInput(enterNumTicketsRequestedPrompt);

      if (chosenPerformanceIDRawString == null) {
        view.displayError("Invalid Performance ID");
        continue;
      }

      if (chosenNumTicketsRawString == null) {
        view.displayError("Invalid ticket number entered");
        continue;
      }

      // If here, can now parse to correct types
      try {
        chosenPerformanceID = Long.parseLong(chosenPerformanceIDRawString);
      } catch (NumberFormatException e) {
        view.displayError("Performance ID entered in invalid format."
            + "Please enter the performance ID exactly with no other characters.");
        continue;
      }

      try {
        chosenNumTickets = Integer.parseInt(chosenNumTicketsRawString);
      } catch (NumberFormatException e) {
        view.displayError("Invalid ticket number entered.");
        continue;
      }

      // check if number of tickets asked for is a positive integer
      if (chosenNumTickets <= 0) {
        view.displayError("Invalid number of tickets requested");
        continue;
      }

      chosenPerformance = getPerformanceByID(chosenPerformanceID);

      if (chosenPerformance == null) {
        view.displayError("Performance with given number does not exist.");
      }

      else {
        // call check if booking possible method
        possible = checkIfBookingPossible(chosenPerformance, chosenNumTickets);

        if (!possible) {
          // make sure no chosen performance data sticking around for next loop iteration
          chosenPerformance = null;
        }
      }


    }

    // exit while
    // user input is now valid
    // now onto actually  creating the booking

    //get the current student
    // already checked if Student so type casting is safe here
    Student student = (Student) getCurrentUser();

    //calculate transaction amount
    double ticketPrice = chosenPerformance.getFinalTicketPrice();
    double transactionAmount = ticketPrice * chosenNumTickets;

    //create the booking
    Booking booking = new Booking(
        nextBookingNumber,
        chosenNumTickets,
        transactionAmount,
        student,
        chosenPerformance);

    // increment for next booking
    nextBookingNumber++;

    // adding bookings to system
    // even if status changed to no longer active later, still keeping them on the system
    // for possible auditing and logging purposes (also sequence diagram requires it)
    // add booking to Booking controller collection of all bookings in the system
    addBooking(booking);  // Booking controller addBooking method

    // add booking to student's collection of bookings
    student.addBooking(booking);

    //add booking to performance object
    chosenPerformance.addBooking(booking);


    //get info needed for payment
    String eventTitle = chosenPerformance.getEventTitle();
    String studentEmail = student.getEmail();
    int studentPhone = student.getPhoneNumber();  // method comes from student superclass (User)
    String epEmail = chosenPerformance.getOrganiserEmail();

    //process payment through the external payment system
    boolean paymentSuccessful = paymentSystem.processPayment(
        chosenNumTickets,
        eventTitle,
        studentEmail,
        studentPhone,
        epEmail,
        transactionAmount);

    if (!paymentSuccessful) {

      //payment failed
      view.displayError("There was an issue with payment");
      booking.cancelPaymentFailed();  // update status field of Booking object
      return;  // end bookPerformance process in failure

    }

    //payment successful - update the number of tickets sold
    int numTicketsSold = chosenPerformance.getNumTicketsSold();
    chosenPerformance.setNumTicketsSold(numTicketsSold + chosenNumTickets);

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
    allBookingsInSystem.add(b);
  }

  private Performance getPerformanceByID(long performanceID) {
    // check if the input performance ID matches any on the system
    for(Performance performance : this.performances) {
      long performanceIDOnSystem = performance.getPerformanceID();

      if(performanceIDOnSystem == performanceID) {
        return performance;
      }
    }
    return null;
  }

  private boolean checkIfBookingPossible(Performance performance, int numTickets) {
    boolean isTicketed = performance.checkIfEventIsTicketed();

    if (!isTicketed) {
      view.displayError("The requested performance is not ticketed."
          + "There is no need to book it");
      return false;
    }

    boolean enoughTicketsLeft = performance.checkIfTicketsLeft(numTickets);

    if (!enoughTicketsLeft) {
      view.displayError("Requested performance has not enough tickets left.");
      return false;
    }

    // if survive until here, desired booking is possible
    return true;
  }

  private Collection<Booking> findBookingsByEventID(long eventID) {
    return null;
  }

  private Booking getBookingByNumber(long bookingNumber) {
    return null;
  }
}

