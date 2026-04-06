package com.fortytwogroup.controller;

import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.model.EntertainmentProvider;
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
  private Collection<Performance> performances;// ref to collection shared with booking controller
  private MockPaymentSystem mockPaymentSystem;

  // dependency injection
  private TextUserInterface textUserInterface;


  public EventPerformanceController(
      TextUserInterface textUserInterface,
      Collection<Performance> performances,
      MockPaymentSystem mockPaymentSystem) {
      this.textUserInterface = textUserInterface;
      this.nextEventID = 1;
      this.nextPerformanceID = 1;
      this.performances = performances;   // ref to collection shared with booking controller
      this.mockPaymentSystem = mockPaymentSystem;

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
    Collection<Performance> newPerformances = new ArrayList<>();

    // instantiate Event object now to make logic easier
    // can destroy it and its performances later
    // will add to relevant places once all checks complete and passed
    Event event = new Event(
        nextEventID,
        eventTitle,
        eventType,
        eventIsTicketed,
        ((EntertainmentProvider) getCurrentUser())
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
      else if(userInput.equalsIgnoreCase("no") && !newPerformances.isEmpty()) {
        addAnotherPerformance = false;
      }
      else if(userInput.equalsIgnoreCase("no") && newPerformances.isEmpty()) {
        textUserInterface.displayError("Events must have at least one performance.");
        continue;
      }
      else {
        Performance performance = getPerformanceDetailsFromEP(event);
        newPerformances.add(performance);
      }
    }


    /* now need to check that an event with the same name and at least one matching
     performance time doesn't already exist */
    boolean titleAndDateClash = false;
    for (Performance performance : performances) {

      /* if start and end time both match any of those in the existing performances,
         check if event titles also match */
      for (Performance newPerformance : newPerformances) {
        if (performance.getStartDateTime().equals(newPerformance.getStartDateTime())
        && performance.getEndDateTime().equals(newPerformance.getEndDateTime())) {
          // check if event title matches
          if(performance.getEventTitle().equalsIgnoreCase(newPerformance.getEventTitle())){
            titleAndDateClash = true;
          }
        }
      }

    }



    // if clash, ask if the EP wants to either change the new event name or scrap the new event
    if(titleAndDateClash){
      String contingencyInput = "";
      while (contingencyInput.isEmpty() ||
          (!contingencyInput.equalsIgnoreCase("change_event_title")
          && !contingencyInput.equalsIgnoreCase("give_up"))) {
        textUserInterface.displayError("Desired event title clashes with an existing event"
            + "that has a performance with the same start and end time as the event being created.");
        contingencyInput = textUserInterface.getInput(
            "Do you wish to change the event title or give up"
                + "on event creation? (change_event_title/give_up): ");

        if (contingencyInput.isEmpty()) {
          textUserInterface.displayError("No input provided."
              + " Please choose either 'change_event_title' or 'give_up'");
          continue;
        }

        if (contingencyInput.equalsIgnoreCase("change_event_title")) {

          String newEventTitle = "";

          boolean validNewEventTitle = false;
          while(!validNewEventTitle){
            newEventTitle = textUserInterface.getInput("Enter new event title: ");

            if(newEventTitle.isEmpty()){
              textUserInterface.displayError("No input provided. "
                  + "Please provide a new event title.");
              continue;
            }

            if (newEventTitle.equalsIgnoreCase(event.getEventTitle())) {
              textUserInterface.displayError("Please choose a new event title.");
              continue;
            }
            validNewEventTitle = true;
          }

          // now set event title to newEventTitle value
          event.setEventTitle(newEventTitle);

          // Success if here


          // add event to the correct EP
          EntertainmentProvider eP = (EntertainmentProvider) getCurrentUser();
          eP.addEvent(event);

          textUserInterface.displaySuccess("Event created successfully");

          // add new performances to controller master list
          this.performances.addAll(event.getPerformancesCollection());

        }

        else if (contingencyInput.equalsIgnoreCase("give_up")) {
          // need to destroy event object and its performances
          // do this by not adding storing it in any of the collections
          return;
        }

      }


    }
    else {
      textUserInterface.displaySuccess("Event created successfully");

      // add event to the correct EP
      EntertainmentProvider eP = (EntertainmentProvider) getCurrentUser();
      eP.addEvent(event);

      // add performance details to event
      this.performances.addAll(event.getPerformancesCollection());



    }

  }

  /**
     * The student can search for performances that are happening on a specific date.
     * They type the day in the YYYY-MM-DD format.
     * The system knows the day a specific Performance object has by getting the 
     * substring of the toString()'ed performance object at the appropriate section.
     * dateString is the date of the performances that the student wishes to search for.
     * 
     * @return                  Group of performances, that have a performance on that date and fit the student preferences, are printed line by line.
     */
  public void searchForPerformances() {
    String dateString = textUserInterface.getInput("Enter start date to search for as YYYY-MM-DD: ");

        if (!checkValidEventDateStringFormat(dateString)) {
          textUserInterface.displayError("Invalid date format. Ensure a proper date is entered.");
          return;
        }

        LocalDate date = LocalDate.parse(dateString);
        String performances = "";

        for (Performance p : this.performances) {
          String pString = p.toString();

          // Getting the string of the performance then getting the substring containing the dates.
          int startDateStartIndex = pString.indexOf("Start: ") + "Start: ".length();
          int startDateEndIndex = pString.indexOf("\nEnd: "); 

          LocalDate startDate = null;
          try {
              startDate = date.parse(pString.substring(startDateStartIndex, startDateEndIndex));
          } catch (Exception e) {
            textUserInterface.displayError("The event date is unreadable."); // Should only happen if the event was never properly instanciated
            break;
          }

          int endDateStartIndex = pString.indexOf("End: ") + "End: ".length();
          int endDateEndIndex = pString.indexOf("\nPerformers: ");

          LocalDate endDate = null;
          try {
              endDate = date.parse(pString.substring(endDateStartIndex, endDateEndIndex));
          } catch (Exception e) {
            textUserInterface.displayError("The event data is corrupted."); // Should only happen if the event was never properly instanciated
          }

          if ((date.isBefore(endDate)||date.equals(endDate)) && (date.isAfter(startDate)||date.equals(startDate))) { // If the performance is on during a day that we are searching for.
            performances += (
              "Performance: " + p.getEventTitle() +"\n"
              + "ID: " + p.getPerformanceID() +"\n"
              + "Start time: " + p.getStartDateTime() +"\n"
              + "End time: " + p.getEndDateTime() +"\n"
              + "Venue: " + p.getVenueAddress() + "\n"
              + "Provider: " + p.getEvent().getOrganiserEmail() +"\n"
              + "Event's average rating: " + p.getEvent().getAverageRatingOfPerformances() +"\n"
            );
          } else {textUserInterface.displayError("No results matching that date.");}
        }
        textUserInterface.displaySpecificPerformance(performances);
  }

  public void viewPerformance() {
    long performanceId = Long.parseLong(textUserInterface.getInput("Enter performance ID: "));
    Performance p = getPerformanceByID(performanceId);


    if (p == null) {
      textUserInterface.displayError("No performance found with that ID");
      return;
    }

    textUserInterface.displaySpecificPerformance(p.toString());

  }

  /**
     * Cancels the performance by getting the performance id, searching for a 
     * performance that matches that ID, and then passing the details of all
     * the students that need to get refunded to the payment system. Once 
     * the EP knows it was a success, the use case ends.
     * 
     */
  public void cancelPerformance() {
    long cancelledPerformanceID;
    try {
        cancelledPerformanceID = Long.parseLong(textUserInterface.getInput("Enter ID of performance to cancel: "));
    } catch (NumberFormatException e) {
      textUserInterface.displayError("That is not a valid performance ID.");
      return;
    }
    Performance cancelledPerformance = getPerformanceByID(cancelledPerformanceID);

    if (cancelledPerformance == null) {
      textUserInterface.displayError("No performance found with that ID");
      return;
    }

    EntertainmentProvider currentUser = (EntertainmentProvider) getCurrentUser();

    String email = currentUser.getEmail();
    Boolean sameEP = cancelledPerformance.checkCreatedByEP(email);
    if (sameEP) { // If the EP requesting to cancel is the one that created the event
      if (cancelledPerformance.checkHasNotHappenedYet()){ // and the event has not happened yet

        String organiserMessage = textUserInterface.getInput("Provide a cancellation method for affected students: ");
        if (organiserMessage == null) {
          textUserInterface.displayError("Please provide a non-empty message for the students.");
          return;
        }

        Boolean hasActiveBookings = cancelledPerformance.hasActiveBookings();
        if (hasActiveBookings) {
          String eventTitle = cancelledPerformance.getEventTitle();
          String bookingDetailsForRefund = cancelledPerformance.getBookingDetailsForRefund();
          for (String refundedBooking : bookingDetailsForRefund.split("\n---\n")){
            int numTickets = Integer.parseInt(refundedBooking.substring(refundedBooking.indexOf("Number of tickets purchased: ") + "Number of tickets purchased: ".length()));

            double transactionAmount = Double.parseDouble(refundedBooking.substring(refundedBooking.indexOf("Amount paid: ") + "Amount paid: ".length(), refundedBooking.indexOf("\nNumber of tickets purchased: ")));

            String studentDetails = refundedBooking.substring(refundedBooking.indexOf("Student details: ") + "Student details: ".length(), refundedBooking.indexOf("\nAmount paid: "));
            String studentEmail = studentDetails.substring(studentDetails.indexOf("Student email: ") + "Student email: ".length(), studentDetails.indexOf("\n"));
            int studentPhone = Integer.parseInt(studentDetails.substring(studentDetails.indexOf("Student phone: ") + "Student phone: ".length()));

            Boolean refundSuccessful = mockPaymentSystem.processRefund(numTickets, eventTitle, studentEmail, studentPhone, email, transactionAmount, organiserMessage);
            if (refundSuccessful) {
              cancelledPerformance.cancel();
              textUserInterface.displaySuccess("Cancellation Successful!");
            }
          }
        }
      }else{
        textUserInterface.displayError("Performance can't be cancelled as it has already happened.");
      }
    }
    else{
      textUserInterface.displayError("You cannot cancel a performance that you did not create.");
    }
  }

  private boolean checkIfSponsorshipPossible(Performance performance, double amount) {
    return (performance.checkIfEventIsTicketed());
  }

  public void sponsorPerformance() {

    // defensive check, only admins can sponsor a performance
    if (!checkCurrentUserIsAdmin()) {
      textUserInterface.displayError("Only admins can sponsor a performance. ");
      return;
    }

    long performanceID = Long.parseLong(textUserInterface.getInput("Enter the performance ID: "));
    double sponsorshipAmount = Double.parseDouble(textUserInterface.getInput("Enter the sponsorship amount: "));

    Performance sponsoredPerformance = getPerformanceByID(performanceID);
    if (sponsoredPerformance == null) {
      textUserInterface.displayError("Performance with given number does not exist");
      return;
    }
    if (checkIfSponsorshipPossible(sponsoredPerformance, sponsorshipAmount)){
      double ticketPrice = sponsoredPerformance.getFinalTicketPrice();
      if (sponsorshipAmount < 0 || sponsorshipAmount > ticketPrice){
        textUserInterface.displayError("The amount provided is invalid.");
        return;
      }
      sponsoredPerformance.sponsor(sponsorshipAmount); // All checks passed: performance can be sponsored
      textUserInterface.displaySuccess("Sponsorship successful!");
    }else{
      textUserInterface.displayError("The requested performance's event is non ticketed. It cannot be sponsored.");
    }

    
  }

  private void addEvent(Event e) {
  }

  private void addPerformance(Performance p) {
  }

  private Event getEventByID(long eventID) {

    for (Performance p : this.performances) {
      if (p.getEvent().getEventId() == eventID) {
        return p.getEvent();

      }
    }
    return null;
  }

  private Event getEventByTitle(String title) {
    for (Performance p : this.performances) {
      if (p.getEvent().getEventTitle().equalsIgnoreCase(title)) {
        return p.getEvent();

      }
    }
    return null;
  }

  private Performance getPerformanceByID(long performanceID) {
    for (Performance p : this.performances) {
      if (p.getPerformanceId() == performanceID) {
        return p;
      }
    }
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
    if (venueDetailsAsStrings.size() == numVenueDetails) {
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
        ticketPrice,
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
        if(isTicketedRawInputString.equalsIgnoreCase("yes")){
          performanceTicketedStatus = true;
        }
        else{
          performanceTicketedStatus = false;
        }
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
