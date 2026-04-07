package com.fortytwogroup.model;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Class representing Entertainment Providers who can register on the app in order to promote
 * their events to students
 */
public class EntertainmentProvider extends User {
  private String orgName;
  private String businessNumber;
  private String name;
  private String description;
  private Collection<Event> events;

  public EntertainmentProvider(
          String orgName,
          String businessNumber,
          String name,
          String description,
          String email,
          String password) {
    super(email, password);
    this.orgName = orgName;
    this.businessNumber = businessNumber;
    this.name = name;
    this.description = description;

    this.events = new ArrayList<>();

  }

  public String getOrgName() {
    return orgName;
  }

  public void addEvent(Event event) {
    this.events.add(event);
  }

  public String getBusinessNumber() {
    return businessNumber;
  }

  public Collection<Event> getEvents() {
    return events;
  }
}
