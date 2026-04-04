package com.fortytwogroup.controller;

import com.fortytwogroup.model.Event;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.enums.EventType;
import com.fortytwogroup.view.TextUserInterface;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EventPerformanceController extends Controller {
  private long nextEventID;
  private long nextPerformanceID;

  // dependency injection
  private TextUserInterface textUserInterface;

  public EventPerformanceController(TextUserInterface textUserInterface) {
    this.textUserInterface = textUserInterface;
    this.nextEventID = 1;
    this.nextPerformanceID = 1;
  }




  public void createEvent() {
    boolean userIsEP = super.checkCurrentUserIsEntertainmentProvider();

    if (!userIsEP) {
      textUserInterface.displayError(
          "Only EntertainmentProvider can create events");
      return;
    }

    String eventTitle = null;
    EventType eventType = null;
    Boolean eventIsTicketed = null;  // Big Boolean type so can be null, use autoboxing later

    while (eventTitle == null || eventType == null || eventIsTicketed == null) {

      // input validation for entered fields
      if (eventTitle == null) {
        String eventTitleRawInput = textUserInterface.getInput(
            "Enter event title: ");

        if (!checkValidTitle(eventTitleRawInput)) {
          continue;
        }
        else{
          eventTitle = eventTitleRawInput.trim();
        }
      }

      if (eventType == null) {
        String eventTypeRawInputString = textUserInterface.getInput(
            "Enter event type: ");

        if (!checkValidEventTypeString(eventTypeRawInputString)) {
          continue;
        }
        else{
          eventType = EventType.valueOf(eventTypeRawInputString.trim().toUpperCase());
        }

      }

      if (eventIsTicketed == null) {
        String eventIsTicketedRawInput = textUserInterface.getInput(
            "Is the event ticketed ('yes'/'no'): ");

        if(!checkValidTicketedStatus(eventIsTicketedRawInput)){
          continue;
        }
        else{
          eventIsTicketed = eventIsTicketedRawInput.toLowerCase().trim().equals("yes");
        }
      }

    }

    // now ask EP to add performances to the event
    boolean addAnotherPerformance = true;
    Collection<Performance> performances = new ArrayList<>();

    // instantiate Event object now to make logic easier
    // can destroy it and its performances later
    // will add to relevant places once all checks complete and passed
    Event event = new Event(
        nextEventID,
        eventTitle,
        eventType,
        eventIsTicketed
    );

    // now add the performances to that event
    while (addAnotherPerformance) {
      String userInput =
          textUserInterface.getInput("Do you wish to add another performance (yes/no): ");

      if (userInput.isEmpty()){
        textUserInterface.displayError("No response provided."
            + "Please enter either yes or no");
        continue;
      }
      else if(userInput.equalsIgnoreCase("no") && !performances.isEmpty()) {
        addAnotherPerformance = false;
      }
      else if(userInput.equalsIgnoreCase("no") && performances.isEmpty()) {
        textUserInterface.displayError("Events must have at least one performance.");
        continue;
      }
      else {
        Performance performance = getPerformanceDetailsFromEP(event);
        performances.add(performance);
      }
    }

    // now need to check that event object doesn't already exist

  }

  public void searchForPerformances() {

  }

  public void viewPerformance() {

  }

  public void cancelPerformance() {

  }

  private boolean checkIfSponsorshipPossible(Performance performance, int amount) {
    return false;
  }

  public void sponsorPerformance() {

  }

  private void addEvent(Event e) {

  }

  private void addPerformance(Performance p) {

  }

  private Event getEventByID(long eventID) {
    return null;
  }

  private Event getEventByTitle(String title) {
    return null;
  }

  private Performance getPerformanceByID(long performanceID) {
    return null;
  }

  private Performance getPerformanceDetailsFromEP(Event event) {
    // get and unpack date/time values
    List<LocalDateTime> startEndDate = getPerformanceDateTimeDetailsFromEP();
    LocalDateTime startDateTime = startEndDate.get(0);
    LocalDateTime endDateTime = startEndDate.get(1);

    // now check if the performance is ticketed
    boolean isTicketed = getPerformanceTicketedStatusFromEP();

    // get number of tickets and ticket price if event is ticketed
    int numTicketsForPerformance = 0;
    double ticketPrice = 0;

    if (isTicketed) {
      numTicketsForPerformance = getNumTicketsAvailableFromEP();
      ticketPrice = getTicketPriceFromEP();
    }

    // get the names of the performers from the EP
    Collection<String> performers = getPerformerNamesFromEP();

    // get the venue details from the ep as a collection of strings then unpack
    ArrayList<String> venueDetailsAsStrings = getVenueDetailsFromEP();

    int numVenueDetails = 4;

    boolean venueOutdoorsStatus;  // if true, venue is outdoors
    boolean venueSmokingStatus;  // if true, venue allows smoking
    String venueAddress;
    int venueCapacity;

    // unpacking venue details to correct types
    if (venueDetailsAsStrings.size() != numVenueDetails) {
      if (venueDetailsAsStrings.getFirst().equalsIgnoreCase("outdoors")){
        venueOutdoorsStatus = true;
      }
      else {
        venueOutdoorsStatus = false;
      }

      if (venueDetailsAsStrings.get(1).equalsIgnoreCase("yes")) {
        venueSmokingStatus = true;
      }
      else {
        venueSmokingStatus = false;
      }

      venueAddress = venueDetailsAsStrings.get(2);

      venueCapacity = Integer.parseInt(venueDetailsAsStrings.get(3));

    }
    else{
      textUserInterface.displayError("internal system error");
      return null;
    }

    // now have all the performance details we need to create the object
    Performance newPerformance = event.createPerformance(
        nextPerformanceID,
        startDateTime,
        endDateTime,
        performers,
        venueAddress,
        venueCapacity,
        venueOutdoorsStatus,
        venueSmokingStatus,
        numTicketsForPerformance,
        numTicketsForPerformance,
        event);

    nextPerformanceID++;

    return newPerformance;
  }

  private List<LocalDateTime> getPerformanceDateTimeDetailsFromEP(){
    // loop until giving correct details gives exit
    while (true) {
      // get start date and time of performance from user
      LocalDate startDate = createDateObject("start");
      LocalTime startTime = createTimeObject("start");

      // get end date and time of performance from user
      LocalDate endDate = createDateObject("end");
      LocalTime endTime = createTimeObject("end");

      LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
      LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

      if(startDateTime.isAfter(endDateTime)) {
        textUserInterface.displayError("Start date/time cannot be after end date/time");
      }
      else{
        return Arrays.asList(startDateTime, endDateTime);
      }
    }

  }

  private LocalDate createDateObject(String startOrEnd) {
    boolean validDate = false;
    String performanceDateRawString = "";
    while(!validDate) {
      performanceDateRawString =  textUserInterface.getInput(
          "Enter performance "+ startOrEnd +" date (YYYY-MM-DD): ");

      validDate = checkValidEventDateStringFormat(performanceDateRawString);
    }

    return LocalDate.parse(performanceDateRawString);
  }

  private LocalTime createTimeObject(String startOrEnd) {
    boolean validTime = false;
    String performanceTimeRawString = "";
    while(!validTime) {
      performanceTimeRawString =  textUserInterface.getInput(
          "Enter Performance "+ startOrEnd +" time (HH:MM): ");

      validTime = checkValidTimeStringFormat(performanceTimeRawString);
    }

    return LocalTime.parse(performanceTimeRawString);
  }


  private boolean getPerformanceTicketedStatusFromEP() {
    Boolean performanceTicketedStatus = null;
    while (performanceTicketedStatus == null) {
      String isTicketedRawInputString = textUserInterface.getInput(""
          + "Is the performance ticketed ('yes'/'no'): ");

      if(!checkValidTicketedStatus(isTicketedRawInputString)){
        continue;
      }
      else{
        performanceTicketedStatus = Boolean.parseBoolean(isTicketedRawInputString);
      }
    }
    return performanceTicketedStatus;
  }

  private int getNumTicketsAvailableFromEP() {
    Integer numTicketsAvailable = null;
    while(numTicketsAvailable == null) {
      String numTicketsRawString = textUserInterface.getInput(
          "Enter number of tickets available for the performance: ");

      try{
        numTicketsAvailable = Integer.parseInt(numTicketsRawString);

        if (numTicketsAvailable <= 0) {
          textUserInterface.displayError("Need to have a positive number of tickets");
          continue;
        }
        else{
          return numTicketsAvailable;
        }
      }
      catch (NumberFormatException e) {
        textUserInterface.displayError("Please enter a valid integer");
        continue;
      }
    }
    return numTicketsAvailable;

  }

  private double getTicketPriceFromEP() {

    while(true) {
      String ticketPriceRawString = textUserInterface.getInput(
          "Enter the ticket price for the performance: ");
      try{
        double ticketPrice = Double.parseDouble(ticketPriceRawString);

        if(ticketPrice <= 0) {
          textUserInterface.displayError(
              "Ticket price must be larger than 0");
          continue;
        }

        // check input is not bigger than a double can store
        if (Double.isInfinite(ticketPrice)) {
          textUserInterface.displayError("Ticket price entered is too high");
          continue;
        }
        // Need toString to avoid precision being messed up when parsed and wrapped
        BigDecimal ticketPriceBigDecimal = new BigDecimal(Double.toString(ticketPrice));
        if (ticketPriceBigDecimal.scale() > 2){
          textUserInterface.displayError(
              "Ticket price cannot have more than 2 decimal places");
          continue;
        }


        // if got to here, ticket price is valid
        return ticketPrice;
      }
      catch (NumberFormatException e) {
        textUserInterface.displayError("Ticket price entered is invalid");
      }
    }
  }


  private Collection<String> getPerformerNamesFromEP(){

    Collection<String> performerNames = new ArrayList<String>();
    while(performerNames.isEmpty()) {
      textUserInterface.getInput("Enter the performer names for the performances."
          + "enter 'no_more_performers' when done");
      String userInput = "";

      while(
          (userInput.isEmpty()) || !userInput.equalsIgnoreCase("no_more_performers") && !userInput.equalsIgnoreCase("'no_more_performers'")
      ) {
        userInput = textUserInterface.getInput("Enter performer name: ");

        if (userInput.equalsIgnoreCase("no_more_performers")
        || userInput.equalsIgnoreCase("'no_more_performers'")) {
          break;
        }
        else if (userInput.isEmpty()) {
          textUserInterface.displayError("Performer name entered is empty");
          continue;
        }
        else{
          performerNames.add(userInput);
        }
      }

      if(performerNames.isEmpty()) {
        textUserInterface.displayError("No performer names entered");
        continue;
      }
    }
    return performerNames;
  }

  // remember to convert to required types when unpacking string collection
  private ArrayList<String> getVenueDetailsFromEP(){
    textUserInterface.getInput("Enter the venue's details for the performances.");

    // ask if the venue is indoors or outdoors (indoors/outdoors)
    String outdoorsStatus = getOutdoorsStatusAsStringFromEP();

    // ask if the venue allows smoking (yes/no)
    String smokingStatus = getSmokingStatusAsStringFromEP();

    // ask for the venue address
    String venueAddressString = getVenueAddressStringFromEP();

    // ask for venue capacity
    String venueCapacityString = getVenueCapacityAsStringFromEP();

    ArrayList<String> venueDetailsAsStrings = new ArrayList();
    venueDetailsAsStrings.add(outdoorsStatus);
    venueDetailsAsStrings.add(smokingStatus);
    venueDetailsAsStrings.add(venueAddressString);
    venueDetailsAsStrings.add(venueCapacityString);

    return venueDetailsAsStrings;
  }

  private String getOutdoorsStatusAsStringFromEP() {

    String outdoorsStatusString = "";

    while(
        outdoorsStatusString.isEmpty() || (
        !outdoorsStatusString.equalsIgnoreCase("indoors") &&
        !outdoorsStatusString.equalsIgnoreCase("outdoors"))
    ) {
      outdoorsStatusString = textUserInterface.getInput(
          "Is the performance venue indoors or outdoors? "
      );

      if (outdoorsStatusString.isEmpty()) {
        textUserInterface.displayError("Input is empty");
      }
      else if (!outdoorsStatusString.equalsIgnoreCase("indoors") &&
          !outdoorsStatusString.equalsIgnoreCase("outdoors")){
        textUserInterface.displayError(
            "Your response must be either indoors or outdoors");
      }

    }
    return outdoorsStatusString;
  }

  private String getSmokingStatusAsStringFromEP() {

    String smokingStatusString = "";

    while(
        smokingStatusString.isEmpty() || (
            !smokingStatusString.equalsIgnoreCase("yes") &&
                !smokingStatusString.equalsIgnoreCase("no"))
    ) {
      smokingStatusString = textUserInterface.getInput(
          "Does the performance venue allow smoking? "
      );

      if (smokingStatusString.isEmpty()) {
        textUserInterface.displayError("Input is empty");
      }
      else if (!smokingStatusString.equalsIgnoreCase("yes") &&
          !smokingStatusString.equalsIgnoreCase("no")){
        textUserInterface.displayError(
            "Your response must be either yes or no");
      }

    }
    return smokingStatusString;
  }

  private String getVenueAddressStringFromEP() {
    String venueAddressString = "";

    while(venueAddressString.isEmpty()) {
      venueAddressString = textUserInterface.getInput(
          "Please enter the performance venue address: ");

      if (venueAddressString.isEmpty()) {
        textUserInterface.displayError("Input is empty");
      }
    }
    return venueAddressString;
  }


  private String getVenueCapacityAsStringFromEP() {
    String venueCapacityString = "";

    boolean validCapacityString = false;
    while(!validCapacityString) {
      venueCapacityString = textUserInterface.getInput(
          "Please enter the performance venue capacity: "
      );

      if (venueCapacityString.isEmpty()) {
        textUserInterface.displayError("Input is empty");
        continue;
      }

      try {
        int venueCapacity = Integer.parseInt(venueCapacityString);

        if (venueCapacity <= 0) {
          textUserInterface.displayError("Venue capacity must be larger than 0");
          continue;
        }
        else{
          validCapacityString = true;
        }
      } catch (NumberFormatException e) {
        textUserInterface.displayError("Invalid capacity entered");
        continue;
      }
    }

    return venueCapacityString;

  }

  // -------------- Input validation functionality below here --------------


  private boolean checkValidTitle(String title) {
    if (title == null) {
      textUserInterface.displayError("Title cannot be null");
      return false;
    }
    if (title.isEmpty()) {
      textUserInterface.displayError("Title cannot be empty");
      return false;
    }
    return true;
  }

  private boolean checkValidEventTypeString(String eventTypeString) {
    if (eventTypeString == null) {
      textUserInterface.displayError("Event type cannot be null");
      return false;
    }

    for (EventType eventType : EventType.values()) {
      if (eventType.toString().equalsIgnoreCase(eventTypeString)) {
        return true;
      }
    }

    textUserInterface.displayError(
        "Event type is not valid.\n"
            + "Please enter one of the following values: "+ Arrays.toString(EventType.values()));

    return false;
  }

  private boolean checkValidTicketedStatus(String ticketedStatus) {
    if (ticketedStatus == null) {
      textUserInterface.displayError("Ticketed status cannot be null");
      return false;
    }
    String [] validStatusOptions = {"yes","no"};
    if (Arrays.asList(validStatusOptions).contains(ticketedStatus.toLowerCase().trim())) {
      return true;
    }

    textUserInterface.displayError(
        "Ticketed status is not valid.\n"
            + "Please enter one of the following values: "+ Arrays.toString(validStatusOptions)
    );

    return false;
  }


  private boolean checkValidEventDateStringFormat(String eventDate) {
    if (eventDate == null) {
      textUserInterface.displayError("Event date cannot be null");
      return false;
    }

    try{
      LocalDate date = LocalDate.parse(eventDate);
    }
    catch (DateTimeParseException e) {
      textUserInterface.displayError("Event date is not a valid date"
          + "in YYYY-MM-DD format");
      return false;
    }

    return true;
  }

  private boolean checkValidTimeStringFormat(String timeString) {
    if (timeString == null) {
      textUserInterface.displayError("Time cannot be null");
      return false;
    }

    try{
      LocalTime time = LocalTime.parse(timeString);
    }
    catch (DateTimeParseException e) {
      textUserInterface.displayError("Time is not a valid time in HH:MM format");
      return false;
    }

    return true;
  }

}
