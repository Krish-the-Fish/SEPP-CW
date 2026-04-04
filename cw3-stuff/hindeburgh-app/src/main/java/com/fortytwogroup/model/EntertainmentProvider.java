package com.fortytwogroup.model;

public class EntertainmentProvider extends User {
  private String orgName;
  private String businessNumber;
  private String name;
  private String description;

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
  }

  public void addEvent(Event event) {

  }


}
