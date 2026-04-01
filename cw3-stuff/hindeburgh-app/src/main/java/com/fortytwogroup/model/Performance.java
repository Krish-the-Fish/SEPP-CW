package com.fortytwogroup.model;

import com.fortytwogroup.model.enums.PerformanceStatus;

import java.time.LocalDateTime;
import java.util.Collection;

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
