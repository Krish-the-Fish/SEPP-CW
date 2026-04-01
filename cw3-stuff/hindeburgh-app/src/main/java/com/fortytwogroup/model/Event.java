package com.fortytwogroup.model;

import com.fortytwogroup.model.enums.EventType;

import java.time.LocalDateTime;
import java.util.Collection;

public class Event {
  private long eventId;
  private String title;
  private EventType type;
  private boolean isTicketed;

  public Performance createPerformance(
      long performanceId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Collection<String> performerNames,
      String venueAddress,
      int venueCapacity,
      boolean venueIsOutdoors,
      boolean venueAllowsSmoking,
      int numTickets,
      double ticketPrice) {
    return null;
  }

  public Performance getPerformanceById(long performanceId) {
    return null;
  }

  public Collection<String> getInfoOfPerformancesOnDate(LocalDateTime searchDateTime) {
    return null;
  }

  private String getOrganiserName() {
    return null;
  }

  public String getOrganiserEmail() {
    return null;
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
  }

  public String toString() {
    return null;
  }

}
