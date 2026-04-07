package com.fortytwogroup.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fortytwogroup.external.PaymentSystem;
import com.fortytwogroup.model.Booking;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.view.TextUserInterface;

/**
 * Subclass of the abstract Controller class. Handles the majority of operations concerning
 * students booking tickets for performances and cancelling their booking, as well as allowing
 * entertainment provider's to also cancel performances and hence student bookings.
 */
public class BookingController extends Controller {
  private long nextBookingNumber;

  // shared with EventPerformanceController
  private Collection<Performance> performances;

  // collection of all bookings in the system
  private Collection<Booking> allBookingsInSystem;

  private final TextUserInterface textUserInterface;
  private final PaymentSystem paymentSystem;

  // performances collection should come from MenuController

  /**
   * Constructor for booking controller, utilizing dependency injection to abstract the process
   * of getting user input
   * @param textUI reference to an instance of TextUserInterface to get user input
   * @param paymentSystem reference to an instance of MockPayment system to handle all payments
   */
  public BookingController(
      TextUserInterface textUI,
      PaymentSystem paymentSystem) {

    this.nextBookingNumber = 1;  // first booking number should be 1
    this.textUserInterface = textUI;
    this.paymentSystem = paymentSystem;
    this.allBookingsInSystem = new ArrayList<>();

  }

  public void setPerformances(Collection<Performance> performances) {
    this.performances = performances;
  }

  /**
   * Method enabling students to book tickets for a given ticketed performance. If the requested
   * performance is non-ticketed the student end user is notified that they do not
   * need to book tickets for that performance.
   */
  public void bookPerformance() {

    //checks if user is a student
    if (!checkCurrentUserIsStudent()) {
      textUserInterface.displayError("Only students can book performances");
      return;
    }

    String enterPerformanceIDPrompt = "Enter Performance ID ";
    String enterNumTicketsRequestedPrompt = "Enter Number of Tickets ";

    boolean possible = false;
    Performance chosenPerformance = null;
    long chosenPerformanceID = -1;  // make negative so if issue, can be spotted
    int chosenNumTickets = -1;  // make negative so if issue, can be spotted

    //loop: while performance == null or (possible == false and isTicketed == true)
    // chosenPerformance.checkIfEventIsTicketed condition on while should be redundant now
    while(chosenPerformance == null || (!possible && chosenPerformance.checkIfEventIsTicketed())) {

      String chosenPerformanceIDRawString = textUserInterface.getInput(enterPerformanceIDPrompt);
      String chosenNumTicketsRawString = textUserInterface.getInput(enterNumTicketsRequestedPrompt);

      if (chosenPerformanceIDRawString == null) {
        textUserInterface.displayError("Invalid Performance ID");
        continue;
      }

      if (chosenNumTicketsRawString == null) {
        textUserInterface.displayError("Invalid ticket number entered");
        continue;
      }

      // If here, can now parse to correct types
      try {
        chosenPerformanceID = Long.parseLong(chosenPerformanceIDRawString);
      } catch (NumberFormatException e) {
        textUserInterface.displayError("Performance ID entered in invalid format."
            + "Please enter the performance ID exactly with no other characters.");
        continue;
      }

      try {
        chosenNumTickets = Integer.parseInt(chosenNumTicketsRawString);
      } catch (NumberFormatException e) {
        textUserInterface.displayError("Invalid ticket number entered.");
        continue;
      }

      // check if number of tickets asked for is a positive integer
      if (chosenNumTickets <= 0) {
        textUserInterface.displayError("Invalid number of tickets requested");
        continue;
      }

      chosenPerformance = getPerformanceByID(chosenPerformanceID);

      if (chosenPerformance == null) {
        textUserInterface.displayError("Performance with given number does not exist.");
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

    // now check if performance has already been
    // if so, need to return error
    if (chosenPerformance.getEndDateTime().isBefore(LocalDateTime.now())) {
      textUserInterface.displayError("Error: Performance has already happened!");
      return;
    }

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
      textUserInterface.displayError("There was an issue with payment");
      booking.cancelPaymentFailed();  // update status field of Booking object
      return;  // end bookPerformance process in failure

    }

    //payment successful - update the number of tickets sold
    int numTicketsSold = chosenPerformance.getNumTicketsSold();
    chosenPerformance.setNumTicketsSold(numTicketsSold + chosenNumTickets);

    // update the sponsorship amount remaining variable
    double amountOfSponsorshipUsed =
        chosenNumTickets * chosenPerformance.getDiscountAmountPerTicket();

    chosenPerformance.setSponsorshipAmountRemaining(
        (chosenPerformance.getSponsorshipAmountRemaining() - amountOfSponsorshipUsed));

    textUserInterface.displaySuccess("Booking Successful");

    //generate and display booking record
    String bookingRecord = booking.generateBookingRecord();
    textUserInterface.displayBookingRecord(bookingRecord);


  }


  /**
   * Method allowing students to add reviews to performances that they went to.
   */
  public void reviewPerformance() {
    if (!(checkCurrentUserIsStudent())){
      textUserInterface.displayError("Students are the only permitted users to post reviews."); 
      return;
    }

    Long performanceID;
    try {
        performanceID = Long.parseLong(textUserInterface.getInput("Enter the ID of the performance you wish to review: "));
      } catch (NumberFormatException e) {
        textUserInterface.displayError("This is not an valid performance number.");
        return;
      }
    Performance performanceForReview = getPerformanceByID(performanceID);
    if (performanceForReview == null) {
      textUserInterface.displayError("Performance with that ID does not exist.");
      return;
    }
    
    if (!(performanceForReview.checkHasNotHappenedYet())){
      int rating;
      try {
          rating = Integer.parseInt(textUserInterface.getInput("Please rate the performance on a scale of 1-5\n1 - You strongly dislike it\n5 - You strongly like it\n \n Rating: "));
      } catch (NumberFormatException e) {
        textUserInterface.displayError("This is not an valid rating.");
        return;
      }

      String comment = textUserInterface.getInput("Please provide a respectful but informative review on your experience: ");

      if (comment.isBlank()) {
        performanceForReview.review(rating, null);
      }else{
        performanceForReview.review(rating, comment);
      }
      
      textUserInterface.displaySuccess("Review submitted!");
    }
    

  }

  /**
   * Method allowing entertainment providers and students to cancel their own bookings
   */
  public void cancelBooking() {
    if (!(checkCurrentUserIsStudent() || checkCurrentUserIsEntertainmentProvider())){
      textUserInterface.displayError("Only students and entertainment providers can cancel bookings");
      return;
    }

    Long bookingNumber;
    try {
          bookingNumber = Long.parseLong(textUserInterface.getInput("Enter the booking number of the event you wish to cancel: "));
      } catch (NumberFormatException e) {
        textUserInterface.displayError("This is not an valid booking number.");
        return;
      }
    Booking cancelledBooking = getBookingByNumber(bookingNumber);

    if (cancelledBooking == null) {
      textUserInterface.displayError("The booking with this booking number does not exist.");
      return;
    }

    LocalDateTime startDate = cancelledBooking.getPerformance().getStartDateTime(); // Checking 24 hours
    if (LocalDateTime.now().isAfter(startDate.minusHours(24))) {
      textUserInterface.displayError("You cannot cancel a booking that's less than 24 hours away.");
      return;
    }
    

    String email = getCurrentUser().getEmail();
    Boolean sameUser = cancelledBooking.checkBookedByStudent(email);
    
    if (!sameUser) {
      textUserInterface.displayError("The booking with given number does not belong to you.");
      return;
    }

    int phoneNumber = ((Student) getCurrentUser()).getPhoneNumber();
    int tickets = cancelledBooking.getNumTickets();
    String eventTitle = cancelledBooking.getPerformance().getEventTitle();
    String epEmail = cancelledBooking.getPerformance().getEvent().getOrganiserEmail();
    double transactionAmount = cancelledBooking.getAmountPaid();

    Boolean refundSuccessful = paymentSystem.processRefund(tickets, eventTitle, email, phoneNumber, epEmail, transactionAmount, "Cancelled by student.");
    if (refundSuccessful) {
      if (checkCurrentUserIsStudent()){cancelledBooking.cancelByStudent();}
      else if (checkCurrentUserIsEntertainmentProvider()) {cancelledBooking.cancelByProvider();}
    }
    else{
      textUserInterface.displayError("Error: Refund failure");
    }

  }

  /**
   * Method to add a new booking object to the system
   * @param b the new booking object being added to the system
   */
  private void addBooking(Booking b) {
    allBookingsInSystem.add(b);
  }

  /**
   * Retrieves the performance object in the system for a given performanceID.
   * If the performanceID is not on the system, return null instead
   * @param performanceID query numeric ID value that the method checks if it has a corresponding
   *                      Performance object on the system.
   * @return Performance object that has the same performance ID as the query performance ID,
   * if no match in system, return null instead.
   */
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

  /**
   * Checks if booking is able to be made for a given performance. Outcome is dependent on the
   * number of tickets the student wants to book and whether or not the event is ticketed.
   * @param performance the performance that the student wishes to book
   * @param numTickets the number of tickets the student wishes to book for that performance
   * @return true if the booking is possible, otherwise return false
   */
  private boolean checkIfBookingPossible(Performance performance, int numTickets) {
    boolean isTicketed = performance.checkIfEventIsTicketed();

    if (!isTicketed) {
      textUserInterface.displayError("The requested performance is not ticketed."
          + "There is no need to book it");
      return false;
    }

    boolean enoughTicketsLeft = performance.checkIfTicketsLeft(numTickets);

    if (!enoughTicketsLeft) {
      textUserInterface.displayError("Requested performance has not enough tickets left.");
      return false;
    }

    // if survive until here, desired booking is possible
    return true;
  }

  /**
   * Requests the corpus of all bookings that exist for a given event, given the event's ID
   * @param eventID unique integer for identifying the event of choice
   * @return collection of all bookings for that event that exist on the system, regarless of the
   * status of the bookings.
   */
  private Collection<Booking> findBookingsByEventID(long eventID) {

    Collection<Booking> result = new ArrayList<>();

    for (Performance p : performances) {

      if (p.getEvent().getEventId() == eventID) {

        for (Booking b : p.getAllBookings()) {

          result.add(b);
        }

      }

    }

    return result;

  }

  private Booking getBookingByNumber(long bookingNumber) {
    for (Booking b : allBookingsInSystem) {
      if (b.getBookingNumber() == bookingNumber) {
        return b;
      }
    }
    return null;
  }


}

