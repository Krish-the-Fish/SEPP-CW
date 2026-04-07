package com.fortytwogroup.systemtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.MenuController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.Event;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.view.TextUserInterface;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CreateEventSystemTest {

  private TextUserInterface mockTextUserInterface;
  private long nextEventID;
  private long nextPerformanceID;
  private Collection<Performance> performances;// ref to collection shared with booking controller
  private MockPaymentSystem mockPaymentSystem;
  private MenuController menuController;
  private UserController userController;
  private EventPerformanceController eventPerformanceController;
  private MockVerificationService mockVerificationService;
  private MockPaymentSystem paymentSystem;
  private BookingController bookingController;


  @BeforeEach
  public void setUp() {

    mockTextUserInterface = Mockito.mock(TextUserInterface.class);

    mockVerificationService = Mockito.mock(MockVerificationService.class);

    // want to focus on create event so assume that the verification verifies all ep accounts
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);

    paymentSystem = new MockPaymentSystem();
    eventPerformanceController = new EventPerformanceController(mockTextUserInterface,
        paymentSystem);
    eventPerformanceController.setPerformances(new ArrayList<>());

    bookingController = new BookingController(mockTextUserInterface, paymentSystem);

    userController = new UserController(mockTextUserInterface, mockVerificationService);

    menuController = new MenuController(userController, eventPerformanceController,
        bookingController, mockTextUserInterface);

  }

  @Test
  void testCreateEvent_studentCannotCreateEvent() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345",
            "chrisbarnes@school.com", "cb12345",
            "chrisbarnes@school.com", "cb12345");

    userController.login();

    eventPerformanceController.createEvent();

    // verify that the error was displayed, meaning early void return was hit
    Mockito.verify(mockTextUserInterface)
        .displayError("Only EntertainmentProvider can create events");

  }


  @Test
  void testCreateEvent_adminStaffCannotCreateEvent() {
    // one of the pre-registered admin login details
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("joeblogs@school.com", "password123");

    userController.login();

    eventPerformanceController.createEvent();

    // verify that the error was displayed, meaning early void return was hit
    Mockito.verify(mockTextUserInterface)
        .displayError("Only EntertainmentProvider can create events");

  }


  @Test
  void testCreateEvent_EPCanCreateEvent() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);

    // simulate inputs from user for create event use case
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            // inputs for registration call
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            // inputs for login call
            "ep@test.com", "eppass",
            // createEvent inputs
            "My Event",        // title
            "MUSIC",           // event type
            "yes",             // ticketed
            "yes",             // add a performance

            "2026-06-01",      // start date
            "10:00",           // start time
            "2026-06-01",      // end date
            "11:00",           // end time
            "yes",             // performance ticketed
            "100",             // num tickets
            "10.00",           // ticket price
            "Enter the performer names...", // this getInput call
            "John",            // performer name
            "no_more_performers", // done with performers
            "Enter the venue details...",  // this getInput call
            "indoors",         // outdoors status
            "no",              // smoking status
            "123 Street",      // venue address
            "500",             // venue capacity
            "no"               // no more performances
        );

    userController.registerEntertainmentProvider();

    userController.login();

    eventPerformanceController.createEvent();

    // checking if (!userIsEP) doesn't trigger when user is an ep
    Mockito.verify(mockTextUserInterface, Mockito.never())
        .displayError("Only EntertainmentProvider can create events");
  }


  @Test
  void testCreateEvent_defendAgainstNoUserLoggedIn() {
    eventPerformanceController.createEvent();

    // (!userIsEP) clause should also guard against this scenario
    Mockito.verify(mockTextUserInterface)
        .displayError("Only EntertainmentProvider can create events");

  }


  @Test
  void testCreateEvent_eventWithValidInputsCreatedSuccessfully() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Event created successfully");
  }

  @Test
  void testCreateEvent_extremeTicketNumRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes",
            "-1",     // invalid ticket num
            "100",    // valid on retry
            "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Need to have a positive number of tickets");
  }

  @Test
  void testCreateEvent_normalTicketNumAccepted() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "50", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Event created successfully");
  }

  @Test
  void testCreateEvent_noEventsWithoutPerformances() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "no",   // should not allow user to say no when no performances added yet
            "yes",  // now add one
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();

    userController.login();

    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Events must have at least one performance.");
  }

  @Test
  void testCreateEvent_allPerformancesAddedToEvent() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "yes",  // add second performance
            "2026-07-01", "10:00", "2026-07-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "Jane", "no_more_performers",
            "Enter venue", "indoors", "no", "456 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    EntertainmentProvider ep = (EntertainmentProvider) userController.getUsers().get("ep@test.com");
    Event createdEvent = ep.getEvents().iterator().next();
    assertEquals(2, createdEvent.getPerformancesCollection().size(),
        "Event should have 2 performances");
  }

  @Test
  void testCreateEvent_allPerformancesAddedToSystemCollection() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );

    userController.registerEntertainmentProvider();

    userController.login();

    eventPerformanceController.createEvent();

    assertEquals(1, eventPerformanceController.getPerformances().size(),
        "System performances collection should contain the new performance");
  }

  @Test
  void testCreateEvent_invalidEventTitleRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "",           // blank title should be rejected
            "My Event",   // valid on retry
            "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Title cannot be empty");
  }

  @Test
  void testCreateEvent_giveUpOptionProvided() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    // create first event
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // now try clashing event
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "give_up"
        );
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface, Mockito.atLeastOnce())
        .displayError(Mockito.contains("clashes with an existing event"));
  }

  @Test
  void testCreateEvent_terminationUponGiveUpInput() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    // create first event
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // attempt clashing event and give up
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "give_up"
        );
    eventPerformanceController.createEvent();

    EntertainmentProvider ep = (EntertainmentProvider) userController.getUsers().get("ep@test.com");
    assertEquals(1, ep.getEvents().size(),
        "EP should still only have 1 event after giving up on clashing event");
  }

  @Test
  void testCreateEvent_requestNewEventNameUponDateClash() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    // create first event
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // attempt clashing event and change title
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "change_event_title",
            "My New Event"
        );
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface, Mockito.atLeastOnce())
        .displayError(Mockito.contains("clashes with an existing event"));
  }

  @Test
  void testCreateEvent_acceptNewEventTitleUponDateClash() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    // create first event
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // attempt clashing event, change title, verify success
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "change_event_title",
            "My New Event"
        );
    eventPerformanceController.createEvent();

    EntertainmentProvider ep = (EntertainmentProvider) userController.getUsers().get("ep@test.com");
    assertEquals(2, ep.getEvents().size(),
        "EP should have 2 events after successfully changing clashing event title");
  }

  @Test
  void testCreateEvent_negativeTicketPriceRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100",
            "-5.00",  // invalid ticket price
            "10.00",  // valid on retry
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Ticket price must be larger than 0");
  }

  @Test
  void testCreateEvent_nonTicketedDoesntAllowTicketPrice() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "no",  // not ticketed
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "no",  // performance not ticketed should give no ticket price/count asked
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // ticket price should never have been asked for
    Mockito.verify(mockTextUserInterface, Mockito.never())
        .getInput("Enter the ticket price for the performance: ");
  }

  @Test
  void testCreateEvent_blankStringForTitleRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "",         // blank title
            "My Event", // valid on retry
            "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Title cannot be empty");
  }

  @Test
  void testCreateEvent_blankStringForTypeRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event",
            "",         // blank type
            "MUSIC",    // valid on retry
            "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError(Mockito.contains("Event type is not valid"));
  }

  @Test
  void testCreateEvent_nonNumericTicketCountRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes",
            "abc",  // non numeric ticket count
            "100",  // valid on retry
            "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Please enter a valid integer");
  }

  @Test
  void testCreateEvent_negativeTicketCountRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes",
            "-1",   // negative ticket count
            "100",  // valid on retry
            "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Need to have a positive number of tickets");
  }

  @Test
  void testCreateEvent_invalidEventType() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event",
            "INVALID_TYPE",  // invalid event type
            "MUSIC",         // valid on retry
            "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError(Mockito.contains("Event type is not valid"));
  }

  @Test
  void testCreateEvent_dateClashDetected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    // create first event
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // attempt identical event clash should be detected
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "give_up"
        );
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface, Mockito.atLeastOnce())
        .displayError(Mockito.contains("clashes with an existing event"));
  }

  @Test
  void testCreateEvent_endDateBeforeStartDateRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-02", "10:00",  // start date
            "2026-06-01", "10:00",  // end date before start date should be rejected
            "2026-06-01", "10:00",  // valid start on retry
            "2026-06-02", "11:00",  // valid end on retry
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Start date/time cannot be after end date/time");
  }

  @Test
  void testCreateEvent_zeroTicketCountRejected() {
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes",
            "0",    // zero ticket count should be rejected
            "100",  // valid on retry
            "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    Mockito.verify(mockTextUserInterface)
        .displayError("Need to have a positive number of tickets");
  }
}
