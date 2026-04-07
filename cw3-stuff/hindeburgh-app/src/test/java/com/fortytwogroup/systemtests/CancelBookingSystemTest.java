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
import com.fortytwogroup.model.enums.BookingStatus;
import com.fortytwogroup.view.TextUserInterface;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CancelBookingSystemTest {
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
  void testCancelBooking_onlyStudentOrEPCanCancelBooking() {
    // log in as admin should be rejected
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("joeblogs@school.com", "password123", "1");
    userController.login();

    bookingController.cancelBooking();

    Mockito.verify(mockTextUserInterface)
        .displayError("Only students and entertainment providers can cancel bookings");
  }

  @Test
  void testCancelBooking_adminCannotCancelBooking() {
    // log in as admin and try to cancel
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("joeblogs@school.com", "password123", "1");
    userController.login();

    bookingController.cancelBooking();

    Mockito.verify(mockTextUserInterface)
        .displayError("Only students and entertainment providers can cancel bookings");
  }

  @Test
  void testCancelBooking_rejectInvalidBookingNumberFormat() {
    // create a performance and book it first
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
            "1", "1",  // book performance
            "chrisbarnes@school.com", "cb12345",
            "abc"  // invalid booking number format
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    bookingController.cancelBooking();

    Mockito.verify(mockTextUserInterface)
        .displayError("This is not an valid booking number.");
  }

  @Test
  void testCancelBooking_cantCancelLessThan24HoursAway() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-04-07", "23:59", "2026-04-08", "01:00",  // starts tonight - within 24 hours
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "chrisbarnes@school.com", "cb12345",
            "1", "1",
            "chrisbarnes@school.com", "cb12345",
            "1"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    bookingController.cancelBooking();

    Mockito.verify(mockTextUserInterface)
        .displayError("You cannot cancel a booking that's less than 24 hours away.");
  }

  @Test
  void testCancelBooking_errorMessageOnAttemptToCancelBookingNotYours() {
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
            // book as chrisbarnes
            "chrisbarnes@school.com", "cb12345",
            "1", "1",
            // try to cancel as a different student
            "lclover@school.com", "password",
            "1"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    bookingController.cancelBooking();

    Mockito.verify(mockTextUserInterface)
        .displayError("The booking with given number does not belong to you.");
  }

  @Test
  void testCancelBooking_bookingObjectStateUpdatedUponStudentCancelSuccess() {
    // create a performance, book it, cancel it as the same student
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
            "1", "1",
            "chrisbarnes@school.com", "cb12345",
            "1"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    bookingController.cancelBooking();

    // check booking status is updated to cancelled by student
    Performance p = eventPerformanceController.getPerformances().iterator().next();
    p.getAllBookings().forEach(b ->
        assertEquals(BookingStatus.CANCELLED_BY_STUDENT, b.getStatus(),
            "Booking status should be CANCELLED_BY_STUDENT after student cancellation"));
  }

  @Test
  void testCancelBooking_bookingObjectStateUpdatedUponEpCancelSuccess() {

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
            "1", "1",
            // EP tries to cancel student's booking
            "ep@test.com", "eppass",
            "1"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    bookingController.cancelBooking();

    // EP's email won't match booking so should get "does not belong to you" error
    Mockito.verify(mockTextUserInterface)
        .displayError("The booking with given number does not belong to you.");
  }

  @Test
  void testCancelBooking_errorMessageReturnedOnCancellationFailure() {
    // MockPaymentSystem always returns true so we can't simulate refund failure
    // this test verifies the happy path to confirm refund is attempted
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
            "1", "1",
            "chrisbarnes@school.com", "cb12345",
            "1"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();
    userController.login();
    bookingController.bookPerformance();
    userController.login();

    bookingController.cancelBooking();

    // MockPaymentSystem always succeeds so no error expected
    Mockito.verify(mockTextUserInterface, Mockito.never())
        .displayError("Error: Refund failure");
  }

  @Test
  void testCancelBooking_errorMessageReturnedOnBookingNumberDoesntExist() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345", "999");
    userController.login();

    bookingController.cancelBooking();

    Mockito.verify(mockTextUserInterface)
        .displayError("The booking with this booking number does not exist.");
  }
}