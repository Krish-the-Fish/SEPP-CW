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

  public void cancel() {

  }

  public boolean checkIfEventIsTicketed() {
    return true;
  }

  public boolean checkIfTicketsLeft(int numTicketsToBuy) {
    return true;
  }

  public double getFinalTicketPrice() {
    return 0;
  }

  public String getOrganiserEmail(){
    return null;
  }

  public String getEventTitle(){
    return null;
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
    return;
  }

  public String toString() {
      return null;
  }

}
