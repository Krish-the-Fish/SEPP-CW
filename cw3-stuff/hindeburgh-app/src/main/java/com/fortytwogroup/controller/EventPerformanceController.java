package com.fortytwogroup.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.Event;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.enums.EventType;
import com.fortytwogroup.view.TextUserInterface;

public class EventPerformanceController extends Controller {
  private long nextEventID;
  private long nextPerformanceID;

  private MockPaymentSystem mockPaymentSystem;

  private Collection<Event> allEvents;
  private Collection<Performance> allPerformances;

  // dependency injection
  private TextUserInterface textUserInterface;

  public EventPerformanceController(TextUserInterface textUserInterface) {
    
    this.textUserInterface = textUserInterface;
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

    while (addAnotherPerformance) {

    }



  }

  public void searchForPerformances() {
    String searchParameter = textUserInterface.getInput(
                                      "Do you wish to search by..."
                                    + "\n1 - Start Date"
                                    + "\n2 - End Date"
                                    + "\n3 - Performer Name"
                                    + "\n4 - Venue Address"
                                    + "\n5 - Venue Capacity"
                                    + "\n6 - Venue is Outdoors"
                                    + "\n7 - Venue allows smoking"
                                    + "\n8 - Number of tickets available"
                                    + "\n9 - Ticket price"
                                    + "\n10 - If event is finished"
                                    + "\n11 - If event is cancelled\n?\n");
    switch (searchParameter) {
      case "1": // Search by start date. Definitely room to upgrade it for before and after and it's probably a good idea
        String startDateString = textUserInterface.getInput("Enter start date to search for as YYYY-MM-DD: ");

        if (!checkValidEventDateStringFormat(startDateString)) {
          textUserInterface.displayError("Invalid date format. Ensure a proper date is entered.");
          return;
        }

        LocalDate startDate = LocalDate.parse(startDateString);
        for (Performance p : allPerformances) {
          String pString = p.toString();

            /* Getting the indices of the relevant part of the performance 
              string so that I only look at the necessary info and can compare it
             */
          int start = pString.indexOf("Start: ") + "Start: ".length();
          int end = pString.indexOf("\nEnd: ");

          /* In the next block and using the above indices, something along the lines of

            "Start: startDateTime
            End: + endDateTime
            Performers: performer0, performer1, ...
            ..."

            becomes just

            "startDateTime"

            then I can compare to the entered date.
            */
          if (pString.substring(start, end).equals(startDate.toString())) {
            textUserInterface.displaySpecificPerformance(p.toString());
          } else {textUserInterface.displayError("No results matching that date.");}
        }
        break;
      case "2": // Search by end date. Definitely room to upgrade it for before and after and it's probably a good idea
        String endDateRawString = textUserInterface.getInput("Enter end date to search for as YYYY-MM-DD: ");

        if (!checkValidEventDateStringFormat(endDateRawString)) {
          textUserInterface.displayError("Invalid date format. Ensure a proper date is entered.");
          return;
        }

        LocalDate endDate = LocalDate.parse(endDateRawString);
        for (Performance p : allPerformances) {
          String pString = p.toString();
          int start = pString.indexOf("End: ") + "End: ".length();
          int end = pString.indexOf("\nPerformers: ");

          if (pString.substring(start, end).equals(endDate.toString())) {
            textUserInterface.displaySpecificPerformance(p.toString());
          } else {textUserInterface.displayError("No results matching that date.");}
        }
        break;
      case "3": // Search by performer name
        String performerName = textUserInterface.getInput("Enter performer name to search for: ");
        for (Performance p : allPerformances) {
          String pString = p.toString();
          int start = pString.indexOf("Performers: ") + "Performers: ".length();
          int end = pString.indexOf("\nVenue Address: ");

          if (pString.substring(start, end).contains(performerName)) {
            textUserInterface.displaySpecificPerformance(p.toString());
          } else {textUserInterface.displayError("No results matching that performer name.");}
        }
        break;
      case "4": // Search by venue address
        String venueAddress = textUserInterface.getInput("Enter venue address to search for: ");
        for (Performance p : allPerformances) {
          String pString = p.toString();
          int start = pString.indexOf("Venue Address: ") + "Venue Address: ".length();
          int end = pString.indexOf("\nVenue Capacity: ");

          if (pString.substring(start, end).equals(venueAddress)) {
            textUserInterface.displaySpecificPerformance(p.toString());
          } else {textUserInterface.displayError("No results matching that address.");}
        }
        break;
      case "5": // Search by venue capacity. Definitely room to upgrade it for greater than and less than
        String venueCapacityString = textUserInterface.getInput("Enter venue capacity to search for: ");

        try{
          int venueCapacity = Integer.parseInt(venueCapacityString);
          for (Performance p : allPerformances) {
            String pString = p.toString();
            int start = pString.indexOf("Venue Capacity: ") + "Venue Capacity: ".length();
            int end = pString.indexOf("\nVenue is Outdoors: ");

            if (pString.substring(start, end).equals(venueCapacity)) {
              textUserInterface.displaySpecificPerformance(p.toString());
            }
          }
        }
        catch (NumberFormatException e) {
          textUserInterface.displayError("Please enter a valid integer");
          return;
        }
        break;
      case "6": // Search by whether venue is outdoors
        String venueIsOutdoorsString = textUserInterface.getInput("Is the venue outdoors? (yes/no): ");
        Boolean venueIsOutdoors = false;
        switch (venueIsOutdoorsString.toLowerCase()) {
          case "y":
            venueIsOutdoors = true;
            break;
          case "n":
            venueIsOutdoors = false;
            break;
          default:
            textUserInterface.displayError("Incorrect input. Please enter 'y' or 'n'");
            break;
        }
        for (Performance p : allPerformances) {
          String pString = p.toString();
          int start = pString.indexOf("Outdoors: ") + "Outdoors: ".length();
          int end = pString.indexOf("\nSmoking Allowed: ");

          if (pString.substring(start, end).equals(venueIsOutdoors.toString())) {
            textUserInterface.displaySpecificPerformance(p.toString());
          }
        }
        break;
      case "7": // Search by whether venue allows smoking
        String venueAllowsSmokingString = textUserInterface.getInput("Does the venue allow smoking? (yes/no): ");
        Boolean venueAllowsSmoking = false;
        switch (venueAllowsSmokingString.toLowerCase()) {
          case "y":
            venueAllowsSmoking = true;
            break;
          case "n":
            venueAllowsSmoking = false;
            break;
          default:
            textUserInterface.displayError("Incorrect input. Please enter 'y' or 'n'");
            break;
        }
        for (Performance p : allPerformances) {
          String pString = p.toString();
          int start = pString.indexOf("Smoking Allowed: ") + "Smoking Allowed: ".length();
          int end = pString.indexOf("\nTickets Total: ");

          if (pString.substring(start, end).equals(venueAllowsSmoking.toString())) {
            textUserInterface.displaySpecificPerformance(p.toString());
          }
        }
        break;
      case "8": // Search by number of tickets available
        String numTicketsAvailableString = textUserInterface.getInput("Enter number of tickets you are looking for: ");
        try{
          int numTicketsAvailable = Integer.parseInt(numTicketsAvailableString);
          for (Performance p : allPerformances) {
            String pString = p.toString();
            int start = pString.indexOf("Tickets Total: ") + "Tickets Total: ".length();
            int end = pString.indexOf("\nTickets Sold: ");

            if (p.toString().substring(start, end).equals(numTicketsAvailable)) {
              textUserInterface.displaySpecificPerformance(p.toString());
            }
          }
        }
        catch (NumberFormatException e) {
          textUserInterface.displayError("Please enter a valid integer");
          return;
        }
        break;
      case "9": // Search by ticket price
        String ticketPriceString = textUserInterface.getInput("Enter desired ticket price: ");
        try{
          double ticketPrice = Double.parseDouble(ticketPriceString);
          for (Performance p : allPerformances) {
            String pString = p.toString();
            int start = pString.indexOf("Ticket Price: ") + "Ticket Price: ".length();
            int end = pString.indexOf("\nStatus: ");

            if (pString.substring(start, end).equals(Double.toString(ticketPrice))) {
              textUserInterface.displaySpecificPerformance(p.toString());
            }
          }
        }
        catch (NumberFormatException e) {
          textUserInterface.displayError("Please enter a valid number");
          return;
        }
        break;
      case "10": // Search by whether event is finished or not
        String eventIsFinishedString = textUserInterface.getInput("Has the event finished? (yes/no): ");
        Boolean eventIsFinished = false;
        switch (eventIsFinishedString.toLowerCase()) {
          case "y":
            eventIsFinished = true;
            break;
          case "n":
            eventIsFinished = false;
            break;
          default:
            textUserInterface.displayError("Incorrect input. Please enter 'y' or 'n'");
            break;
        }
        for (Performance p : allPerformances) {
          String pString = p.toString();
          int start = pString.indexOf("End: ") + "End: ".length();
          int finished = pString.indexOf("\nPerformers: ");

          // If "the event is finished (true) AND the user wants finished events (true)", OR "if the event is not finished (false) AND the user wants not finished events (false)", then show the performance
          if (LocalDate.parse(pString.substring(start, finished)).isAfter(LocalDate.now()) == eventIsFinished) {
            textUserInterface.displaySpecificPerformance(p.toString());
          }
        }
        break;
      case "11": // Search by whether event is cancelled or not
        String eventIsCancelledString = textUserInterface.getInput("Is the event cancelled? (yes/no): ");
        Boolean eventIsCancelled = false;
        switch (eventIsCancelledString.toLowerCase()) {
          case "y":
            eventIsCancelled = true;
            break;
          case "n":
            eventIsCancelled = false;
            break;
          default:
            textUserInterface.displayError("Incorrect input. Please enter 'y' or 'n'");
            break;
        }
        for (Performance p : allPerformances) {
          String pString = p.toString();
          int start = pString.indexOf("Status: ") + "Status: ".length();
          int end = pString.indexOf("\n");

          // If "the event is cancelled (true) AND the user wants cancelled events (true)", OR "if the event is not active (false) AND the user wants active events (false)", then show the performance
          if (pString.substring(start, end).equals("CANCELLED") == eventIsCancelled) {
            textUserInterface.displaySpecificPerformance(p.toString());
          }
        }
        break;
      }

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

  public void cancelPerformance() {
    long cancelledPerformanceID = Long.parseLong(textUserInterface.getInput("Enter ID of performance to cancel: "));
    Performance cancelledPerformance = getPerformanceByID(cancelledPerformanceID);

    if (cancelledPerformance == null) {
      textUserInterface.displayError("No performance found with that ID");
      return;
    }

    long ID = cancelledPerformance.getPerformanceId();
    if (ID == cancelledPerformanceID) { // Found a match for what the user entered
      EntertainmentProvider currentUser = (EntertainmentProvider) getCurrentUser();
        
      String email = currentUser.getEmail();
      Boolean sameEP = cancelledPerformance.checkCreatedByEP(email);
      if (sameEP) { // If the EP requesting to cancel is the one that created the event
        if (cancelledPerformance.checkHasNotHappenedYet()){

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
  }

  private boolean checkIfSponsorshipPossible(Performance performance, double amount) {
    return (performance.checkIfEventIsTicketed());
  }

  public void sponsorPerformance() {
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
    allEvents.add(e);
  }

  private void addPerformance(Performance p) {
  }

  private Event getEventByID(long eventID) {
    for (Event e : allEvents){
      if (e.getEventId() == eventID) {
        return e;
      }
    }
    return null;
  }

  private Event getEventByTitle(String title) {
    for (Event e : allEvents){
      if (e.toString().contains(title)) { //Probably a better way to do this. I will check over it later.
        return e;
      }
    }
    return null;
  }

  private Performance getPerformanceByID(long performanceID) {
    return null;
  }

  private Performance givePerformanceDetails() {
    // get and unpack date/time values
    List<LocalDateTime> startEndDate = getPerformanceDateTimeDetailsFromEP();
    LocalDateTime startDate = startEndDate.get(0);
    LocalDateTime endDate = startEndDate.get(1);

    // now check if the performance is ticketed
    boolean isTicketed = getPerformanceTicketedStatusFromEP();

    // get number of tickets if event is ticketed
    if (isTicketed) {
      int numTicketsForPerformance = getNumTicketsAvailableFromEP();
    }

    //
    return null;
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
