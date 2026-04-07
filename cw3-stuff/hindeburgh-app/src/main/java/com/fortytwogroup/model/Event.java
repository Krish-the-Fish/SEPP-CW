package com.fortytwogroup.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import com.fortytwogroup.model.enums.EventType;

/**
 * Class used to create objects containing the key details of a given event on the app.
 */
public class Event {
  private long eventId;
  private String title;
  private EventType type;
  private boolean isTicketed;
  private EntertainmentProvider entertainmentProvider;
  private Collection<Performance> performances;


  public Event(
      long eventId,
      String title,
      EventType type,
      boolean isTicketed,
      EntertainmentProvider entertainmentProvider) {
    this.eventId = eventId;
    this.title = title;
    this.type = type;
    this.isTicketed = isTicketed;
    this.performances = new ArrayList<>();  // will add performances as they come in
    this.entertainmentProvider = entertainmentProvider;
  }

  public long getId() {
    return eventId;
  }



  // Contains call to the Performance constructor
  public Performance createPerformance(long performanceId, LocalDateTime startDateTime, LocalDateTime endDateTime,
      Collection<String> performerNames, String venueAddress, int venueCapacity, boolean venueIsOutdoors,
      boolean venueAllowsSmoking, int numTicketsTotal, double ticketPrice, Event event) {
        
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
          ticketPrice,
          event);
        
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
    return entertainmentProvider.getOrgName();
  }

  public String getOrganiserEmail() {
    return entertainmentProvider.getEmail();  //  calling getter within EP parent class
  }

  /**
   * Aggregates all the ratings published by students for a given performance they were at
   * in order to calculate and set the performance's average review score on a scale of 1 to 5
   * on the app.
   * @return average of all the ratings provided by students for a given performance
   */
  public double getAverageRatingOfPerformances() {
    int sumOfRatings = 0;
    for (Performance p : performances) { // Get average rating per performance
      int sumOfPerformanceRatings = 0;   // by summing all the ratings in a performance
      for (int rating : p.getReviewRatings()){
        sumOfPerformanceRatings += rating;
      }                                  // then dividing it by number of reviews 
      sumOfRatings += (sumOfPerformanceRatings / p.getReviewRatings().size()); // then add to the total of average ratings per performance
    }
    sumOfRatings /= performances.size(); // Then divide that total by number of performances
    return sumOfRatings;
  }

  public Collection<String> getAllPerformanceReviews() {
    Collection<String> allReviews = new ArrayList<>();
    for (Performance p : performances) {
      allReviews.addAll(p.getReviewComments());
    }
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

  public void setEventTitle(String newEventTitle) {
    this.title = newEventTitle;
  }

  /**
   * Produces a string literal containing all the details of an event object, including
   * variable names and the values they take on.
   * Method deliberately omits the reference to the entertainment provider object
   * @return String containing key fields within an Event instance
   */
  @Override
  public String toString() {
    return "Event ID: " + eventId + "\n" +
        "Title: " + title + "\n" +
        "Type: " + type + "\n" +
        "Ticketed: " + isTicketed + "\n" +
        "Performances" + performances + "\n";
  }

  public boolean getIsTicketed() {
    return isTicketed;
  }

  // public getter for event title
  public String getEventTitle() {
    return title;
  }

  public Collection<Performance> getPerformancesCollection() {
    return performances;
  }

  public long getEventId() {
    return eventId;
  }
}
