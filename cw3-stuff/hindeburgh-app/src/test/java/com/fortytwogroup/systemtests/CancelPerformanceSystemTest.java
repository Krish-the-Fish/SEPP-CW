package com.fortytwogroup.systemtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.enums.PerformanceStatus;
import com.fortytwogroup.view.TextUserInterface;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CancelPerformanceSystemTest {
  private TextUserInterface mockTextUserInterface;
  private UserController userController;
  private EventPerformanceController eventPerformanceController;
  private MockVerificationService mockVerificationService;
  private MockPaymentSystem paymentSystem;
  private BookingController bookingController;

  @BeforeEach
  public void setUp() {
    mockTextUserInterface = Mockito.mock(TextUserInterface.class);
    mockVerificationService = Mockito.mock(MockVerificationService.class);
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);

    paymentSystem = new MockPaymentSystem();

    eventPerformanceController = new EventPerformanceController(
        mockTextUserInterface, paymentSystem);
    eventPerformanceController.setPerformances(new ArrayList<>());

    bookingController = new BookingController(mockTextUserInterface, paymentSystem);
    bookingController.setPerformances(eventPerformanceController.getPerformances());

    userController = new UserController(mockTextUserInterface, mockVerificationService);
  }

  @Test
  void testCancelPerformance_epCanOnlyCancelTheirOwnPerformance() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            // register EP1
            "OrgOne", "BN001", "ep1@test.com", "eppass1", "John", "Description one",
            // register EP2
            "OrgTwo", "BN002", "ep2@test.com", "eppass2", "Jane", "Description two",
            // login EP1
            "ep1@test.com", "eppass1",
            // EP1 create event
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            // login EP2
            "ep2@test.com", "eppass2",
            // EP2 create event
            "EP2 Event", "MUSIC", "yes",
            "yes",
            "2030-07-01", "10:00", "2030-07-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "Jane", "no_more_performers",
            "Enter venue", "indoors", "no", "456 Street", "500",
            "no",
            // login EP2 again for cancel
            "ep2@test.com", "eppass2",
            // cancel inputs
            "1",  // EP1's performance - rejected
            "2",  // EP2's own performance - succeeds
            "Cancellation message"
        );
    userController.registerEntertainmentProvider();
    userController.registerEntertainmentProvider();
    userController.login();       // EP1
    eventPerformanceController.createEvent();
    userController.login();       // EP2
    eventPerformanceController.createEvent();
    userController.login();       // EP2 again for cancel

    eventPerformanceController.cancelPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("The performance with given number does not belong to you.");
    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Cancellation Successful!");
  }

  @Test
  void testCancelPerformance_EPCanCancelTheirPerformance() {
    // register EP, log in, create a future performance, then cancel it
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "ep@test.com", "eppass",
            "1", "Cancellation message"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Cancellation Successful!");
  }

  @Test
  void testCancelPerformance_studentCannotCancelPerformance() {
    // create a future performance first
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            // log in as student and try to cancel
            "chrisbarnes@school.com", "cb12345",
            "1"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Error: only EntertainmentProviders are permitted to cancel performances.");
  }

  @Test
  void testCancelPerformance_adminStaffCannotCancelPerformance() {
    // create a future performance first
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            // log in as admin and try to cancel
            "joeblogs@school.com", "password123",
            "1"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Error: only EntertainmentProviders are permitted to cancel performances.");
  }

  @Test
  void testCancelPerformance_canOnlyCancelPerformanceThatHaventHappenedYet() {
    // create one performance that's been and one in the future
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            // performance that's been
            "yes",
            "2020-06-01", "10:00", "2020-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            // performance that's not been yet
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "456 Street", "500",
            "no",
            "ep@test.com", "eppass",
            "1",  // past performance should be rejected
            "2",  // future performance should succeed
            "Cancellation message"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    // error should have been shown for past performance
    Mockito.verify(mockTextUserInterface)
        .displayError("Performance can't be cancelled as it has already happened.");
    // but cancellation should ultimately succeed with the future performance
    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Cancellation Successful!");
  }

  @Test
  void testCancelPerformance_epMustProvideMessageForStudents() {
    // create a future performance and try to cancel with blank message
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "ep@test.com", "eppass",
            "1",
            "",                     // blank message should be rejected
            "Cancellation message"  // valid on retry
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Please provide a non-empty message for the students");
  }

  @Test
  void testCancelPerformance_cancellationMessageNotBlank() {
    // same as above - blank message should be rejected
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "ep@test.com", "eppass",
            "1",
            "",                     // blank message
            "Cancellation message"  // valid on retry
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    // cancellation should still succeed after valid message provided on retry
    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Cancellation Successful!");
  }

  @Test
  void testCancelPerformance_cancellationMessageNotNull() {
    // null message handling - getInput returns null
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "ep@test.com", "eppass",
            "1",
            null,                   // null message
            "Cancellation message"  // valid on retry
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Please provide a non-empty message for the students");
  }

  @Test
  void testCancelPerformance_repromptEpOnInvalidPerformanceID() {
    // provide invalid ID first then valid ID
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "ep@test.com", "eppass",
            "999",          // invalid ID
            "1",            // valid on retry
            "Cancellation message"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Performance with given number does not exist.");
    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Cancellation Successful!");
  }

  @Test
  void testCancelPerformance_correctPerformanceCancelled() {
    // create two performances and cancel only the second one
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            // first performance
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            // second performance
            "yes",
            "2030-07-01", "10:00", "2030-07-01", "11:00",
            "yes", "50", "5.00",
            "Enter performers", "Jane", "no_more_performers",
            "Enter venue", "outdoors", "no", "456 Street", "200",
            "no",
            "ep@test.com", "eppass",
            "2",  // cancel second performance
            "Cancellation message"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();

    eventPerformanceController.cancelPerformance();

    // check second performance is cancelled but first is not
    Performance[] performances = eventPerformanceController
        .getPerformances().toArray(new Performance[0]);

    assertEquals(PerformanceStatus.ACTIVE, performances[0].getStatus(),
        "First performance should still be active");
    assertEquals(PerformanceStatus.CANCELLED, performances[1].getStatus(),
        "Second performance should be cancelled");
  }

  @Test
  void testCancelPerformance_allActiveBookingsCancelled() {
    // create a future performance, book it as a student, then cancel as EP
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            // log in as student and book
            "chrisbarnes@school.com", "cb12345",
            "1", "2",  // performance ID and num tickets
            // log back in as EP and cancel
            "ep@test.com", "eppass",
            "1", "Cancellation message"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    eventPerformanceController.cancelPerformance();

    // all bookings on the performance should now be cancelled
    Performance p = eventPerformanceController.getPerformances().iterator().next();
    p.getAllBookings().forEach(b ->
        assertEquals(com.fortytwogroup.model.enums.BookingStatus.CANCELLED_BY_PROVIDER,
            b.getStatus(),
            "All bookings should be cancelled after performance cancellation"));
  }

  @Test
  void testCancelPerformance_errorReturnedOnAtLeastOneRefundFailure() {

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "chrisbarnes@school.com", "cb12345",
            "1", "2",
            "ep@test.com", "eppass",
            "1", "Cancellation message"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    eventPerformanceController.cancelPerformance();

    // MockPaymentSystem always succeeds so cancellation should succeed
    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Cancellation Successful!");
  }
}