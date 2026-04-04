package com.fortytwogroup.model;

import java.time.LocalDateTime;
import java.util.Collection;

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
    double ticketPrice 
    
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
    

  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public long getPerformanceId() {
    return performanceId;
  }
  private Event event;  // reference to the event that contains the performance

  // including inactive bookings in allBookings variable for possible auditing purposes
  private Collection<Booking> allBookings;

  public void cancel() {

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

  public double getFinalTicketPrice() {
    return 0;
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
    return false;
  }

  public boolean checkCreatedByEP(String email) {
    return false;
  }

  public boolean hasActiveBookings() {
    return false;
  }

  public String getBookingDetailsForRefund() {
    return null;
  }

  public void sponsor(double amount) {
    return;
  }

  public void review(int rating, String comment) {
    return;
  }

  public void addBooking(Booking b) {
    allBookings.add(b);
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


  // public getter for numTicketsSold
  public int getNumTicketsSold() {
    return numTicketsSold;
  }

  public void setNumTicketsSold(int newTicketsSoldValue) {
    numTicketsSold = newTicketsSoldValue;
  }


}
