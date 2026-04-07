package com.fortytwogroup.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fortytwogroup.model.enums.BookingStatus;
import com.fortytwogroup.model.enums.PerformanceStatus;

public class Performance {
  private long performanceId;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private Collection<String> performerNames;
  private String venueAddress;
  private int venueCapacity;
  private boolean venueIsOutdoors;
  private boolean venueAllowsSmoking;
  private int numTicketsTotal;
  private int numTicketsSold;
  private double ticketPrice;
  private boolean isSponsored;
  private double sponsoredAmount;
  private Collection<Integer> reviewRatings;
  private Collection<String> reviewComments;
  private PerformanceStatus status;
  private Event event;  // reference to the event that contains the performance
  // including inactive bookings in allBookings variable for possible auditing purposes
  private Collection<Booking> allBookings;  // bookings for just this performance
  private double sponsorshipAmountRemaining;

  public Performance(
    long performanceId, 
    LocalDateTime startDateTime, 
    LocalDateTime endDateTime,
    Collection<String> performerNames, 
    String venueAddress, 
    int venueCapacity, 
    boolean venueIsOutdoors,
    boolean venueAllowsSmoking, 
    int numTicketsTotal, 
    double ticketPrice,
    Event event
    
  ) 
  {
    this.performanceId = performanceId;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.performerNames = performerNames;
    this.venueAddress = venueAddress;
    this.venueCapacity = venueCapacity;
    this.venueIsOutdoors = venueIsOutdoors;
    this.venueAllowsSmoking = venueAllowsSmoking;
    this.numTicketsTotal = numTicketsTotal;
    this.ticketPrice = ticketPrice;
    this.isSponsored = false;
    this.sponsoredAmount = 0;
    this.status = PerformanceStatus.ACTIVE;
    this.event = event;
    this.sponsorshipAmountRemaining = sponsoredAmount;
    this.allBookings = new ArrayList<Booking>();
    this.reviewRatings = new ArrayList<Integer>();
    this.reviewComments = new ArrayList<String>();

  }

  public Collection<Integer> getReviewRatings() {
    return this.reviewRatings;
  }

  public Collection<String> getReviewComments() {
    return this.reviewComments;
  }

  public String getVenueAddress() {
    return this.venueAddress;
  }

  public LocalDateTime getStartDateTime() {
    return this.startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  public long getPerformanceId() {
    return this.performanceId;
  }

  public void cancel() {
    setStatus(PerformanceStatus.CANCELLED);
  }

  private void setStatus(PerformanceStatus updatedStatus) {
    this.status = updatedStatus;
  }

  public boolean checkIfEventIsTicketed() {
    Event event = getEvent();

    if (event == null) {
      return false;
    }

    return event.getIsTicketed();
  }

  public boolean checkIfTicketsLeft(int numTicketsToBuy) {
    int numTicketsLeft = numTicketsTotal - numTicketsSold;

    if (numTicketsToBuy > numTicketsLeft) {
      return false;
    }
    return true;
  }

  /**
   * Method assumes that there are still tickets remaining. Gets final ticket price of a single
   * ticket at time of buying
   * @return amount
   */
  public double getFinalTicketPrice() {
    // will evenly distribute the sponsorship amount among the remaining tickets
    // defensive check to prevent division by zero
    if (numTicketsSold == numTicketsTotal) {
      // no tickets left
      return 0;
    }

    double reductionPerTicket = getDiscountAmountPerTicket();
    double discountedTicketPrice = ticketPrice - reductionPerTicket;

    // make sure we don't accidentally owe them money
    if (discountedTicketPrice < 0){
      return 0;
    }
    else {
      return discountedTicketPrice;
    }

  }

  public double getDiscountAmountPerTicket() {

    // defensive check to avoid division by 0
    if (numTicketsSold == numTicketsTotal) {
      return 0;
    }

    double discountPerTicket = sponsorshipAmountRemaining/(numTicketsTotal - numTicketsSold);
    return discountPerTicket;
  }

  public String getOrganiserEmail(){
    Event event = getEvent();
    return event.getOrganiserEmail();  // returns epEmail
  }

  public String getEventTitle(){
    Event event = getEvent();

    return event.getEventTitle();
  }

  public boolean checkHasNotHappenedYet() {
    return (LocalDateTime.now().isBefore(startDateTime));
  }

  public boolean checkCreatedByEP(String email) {
    return (getOrganiserEmail().equals(email));
  }

  public boolean hasActiveBookings() {
    for (Booking booking : allBookings) {
      if (booking.getStatus() == BookingStatus.ACTIVE) {
        return true;
      }
    }
    return false;
  }

  public String getBookingDetailsForRefund() {

    String bookingDetailsForRefund = "";

    for (Booking b : allBookings) {
      if (b.getStatus() == BookingStatus.ACTIVE) {
        
        bookingDetailsForRefund += (
          "Student details: " + b.getStudentDetails() + "\n" +
          "Amount paid: " + b.getAmountPaid() + "\n" +
          "Number of tickets purchased: " + b.getNumTickets() + "\n---\n"         
        );
      }
    }
    return bookingDetailsForRefund;
  }

  public void sponsor(double amount) {
    if (event.getIsTicketed()) {
      setIsSponsored(true);
      setSponsoredAmount(amount);  // allow for multiple sponsorships
      setSponsoredAmountRemaining(amount);  // allow for multiple sponsorships
    }
  }

  private void setIsSponsored(Boolean updatedSponsorshipStatus) {
    this.isSponsored = updatedSponsorshipStatus;
  }

  private void setSponsoredAmount(double sponsorshpAmount) {
    this.sponsoredAmount += sponsorshpAmount;
  }

  private void setSponsoredAmountRemaining(double sponsorshipAmountRemaining) {
    this.sponsorshipAmountRemaining += sponsorshipAmountRemaining;
  }

  public void review(int rating, String comment) {
    if (checkHasNotHappenedYet() && rating >= 1 && rating <= 5) {
      reviewRatings.add(rating);

      if (comment != null && !comment.isEmpty()) {
        reviewComments.add(comment);
      }
    }
    // It may be necessary to have some sort of moderation for reviews unless we assume that all 
    // reviews are in good faith and not abusive.
  }

  public void addBooking(Booking b) {
    allBookings.add(b);
    numTicketsSold += b.getNumTickets();
  }

  @Override
  public String toString() {
    return "Performance ID: " + performanceId + "\n" +
        "Start: " + startDateTime + "\n" +
        "End: " + endDateTime + "\n" +
        "Performers: " + String.join(", ", performerNames) + "\n" +
        "Venue Address: " + venueAddress + "\n" +
        "Venue Capacity: " + venueCapacity + "\n" +
        "Outdoors: " + venueIsOutdoors + "\n" +
        "Smoking Allowed: " + venueAllowsSmoking + "\n" +
        "Tickets Total: " + numTicketsTotal + "\n" +
        "Tickets Sold: " + numTicketsSold + "\n" +
        "Ticket Price: " + ticketPrice + "\n" +
        "Status: " + status + "\n" +
        "All Active Bookings: "+ allBookings + "\n";
  }

  /**
   * Method chooses not to expose the collection allActiveBookings to respect privacy of
   * students with bookings.
   * @return String containing all the attributes in a given performance class instance apart
   * from allActiveBookings
   * String contains each attribute on its own line.
   */
  public String toStringSensitive() {
    return "Performance ID: " + performanceId + "\n" +
        "Start: " + startDateTime + "\n" +
        "End: " + endDateTime + "\n" +
        "Performers: " + String.join(", ", performerNames) + "\n" +
        "Venue Address: " + venueAddress + "\n" +
        "Venue Capacity: " + venueCapacity + "\n" +
        "Outdoors: " + venueIsOutdoors + "\n" +
        "Smoking Allowed: " + venueAllowsSmoking + "\n" +
        "Tickets Total: " + numTicketsTotal + "\n" +
        "Tickets Sold: " + numTicketsSold + "\n" +
        "Ticket Price: " + ticketPrice + "\n" +
        "Status: " + status + "\n";
  }


  // getter for performance ID
  public long getPerformanceID(){
    return performanceId;
  }

  // public since needs usage in Performance class
  public Event getEvent() {
    return event;
  }

  // getter to make system test implementation easier
  public boolean getIsSponsored() {
    return isSponsored;
  }


  // public getter for numTicketsSold
  public int getNumTicketsSold() {
    return numTicketsSold;
  }

  // getter to make system test easier to implement
  public PerformanceStatus getStatus() {
    return status;
  }

  public void setNumTicketsSold(int newTicketsSoldValue) {
    numTicketsSold = newTicketsSoldValue;
  }

  public Collection<Booking> getAllBookings() {
    return allBookings;
  }

  public double getSponsorshipAmountRemaining() {
    return this.sponsorshipAmountRemaining;
  }

  public void setSponsorshipAmountRemaining(double amount) {
    this.sponsorshipAmountRemaining = amount;
  }

  public double getTicketPrice() {
    return ticketPrice;
  }
}
