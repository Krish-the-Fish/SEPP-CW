package com.fortytwogroup.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fortytwogroup.model.enums.EventType;

public class Event {
  private long eventId;
  private String title;
  private EventType type;
  private boolean isTicketed;
  private EntertainmentProvider entertainmentProvider;

  private ArrayList<Performance> performances = new ArrayList<>();

  public Event(long eventId, String title, EventType type, boolean isTicketed) {
    this.eventId = eventId;
    this.title = title;
    this.type = type;
    this.isTicketed = isTicketed;
  }

  public Performance createPerformance(long performanceId, LocalDateTime startDateTime, LocalDateTime endDateTime,
      Collection<String> performerNames, String venueAddress, int venueCapacity, boolean venueIsOutdoors,
      boolean venueAllowsSmoking, int numTicketsTotal, double ticketPrice) {
        
        Performance newPerformance = new Performance(
          performanceId,
          startDateTime,
          endDateTime,
          performerNames,
          venueAddress,
          venueCapacity,
          venueIsOutdoors,
          venueAllowsSmoking,
          numTicketsTotal,
          ticketPrice
          );
        
        addPerformance(newPerformance);

    return newPerformance;
  }

  public Performance getPerformanceById(long performanceId) {
    for (Performance p : performances) {
      if (p.getPerformanceId() == performanceId) {return p;}
    } return null;
  }

  public Collection<String> getInfoOfPerformancesOnDate(LocalDateTime searchDateTime) {
    ArrayList<String> performanceInfo = new ArrayList<>();
    for (Performance p : performances) {
      if (p.getStartDateTime().equals(searchDateTime)) { // Return only performances on the date that's been searched for.
        performanceInfo.add(p.toString());
      }
    }
    return performanceInfo;
  }

  private String getOrganiserName() {
    return null;
  }

  public String getOrganiserEmail() {
    return entertainmentProvider.getEmail();  //  calling getter within EP parent class
  }

  public double getAverageRatingOfPerformances() {
    return 0;
  }

  public Collection<String> getAllPerformanceReviews() {
    return null;
  }

  private boolean hasPerformanceAtSameTimes(
      LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    return false;
  }

  private void addPerformance(Performance p) {
    performances.add(p);
  }

  /**
   * Method deliberately omits the reference to the entertainment provider object
   * @return String containing key fields within an Event instance
   */
  @Override
  public String toString() {
    return "Event ID: " + eventId + "\n" +
        "Title: " + title + "\n" +
        "Type: " + type + "\n" +
        "Ticketed: " + isTicketed + "\n";
  }

  public boolean getIsTicketed() {
    return isTicketed;
  }

  // public getter for event title
  public String getEventTitle() {
    return title;
  }
}
