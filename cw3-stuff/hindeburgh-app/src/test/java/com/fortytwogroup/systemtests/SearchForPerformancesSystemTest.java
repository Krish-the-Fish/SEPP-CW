package com.fortytwogroup.systemtests;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.view.TextUserInterface;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SearchForPerformancesSystemTest {
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

    paymentSystem = new MockPaymentSystem();
    eventPerformanceController = new EventPerformanceController(mockTextUserInterface,
        paymentSystem);
    eventPerformanceController.setPerformances(new ArrayList<>());

    bookingController = new BookingController(mockTextUserInterface, paymentSystem);

    userController = new UserController(mockTextUserInterface, mockVerificationService);

  }

  @Test
  void testSearchForPerformances_rejectsInvalidDateFormat() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("not-a-date");

    eventPerformanceController.searchForPerformances();

    Mockito.verify(mockTextUserInterface)
        .displayError("Invalid date format. Ensure a proper date is entered.");
  }

  @Test
  void testSearchForPerformances_displayErrorMessageUponInvalidDateInputFormat() {
    // same as above but with wrong date format (DD-MM-YYYY instead of YYYY-MM-DD)
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("01-06-2026");

    eventPerformanceController.searchForPerformances();

    Mockito.verify(mockTextUserInterface)
        .displayError("Invalid date format. Ensure a proper date is entered.");
  }

  @Test
  void testSearchForPerformances_correctPerformanceDisplayedToScreen() {
    // create a performance on 2026-06-01
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
            "no",
            "2026-06-01"  // search for that date
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    eventPerformanceController.searchForPerformances();

    Mockito.verify(mockTextUserInterface)
        .displaySpecificPerformance(anyString());
  }


  @Test
  void testSearchForPerformances_correctIntervalSearched() {
    // create a multi-day performance spanning 2026-06-01 to 2026-06-03
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2026-06-01", "10:00", "2026-06-03", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no",
            "2026-06-02"  // search for a date within the performance interval
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    eventPerformanceController.searchForPerformances();

    // performance spans this date so should be displayed
    Mockito.verify(mockTextUserInterface)
        .displaySpecificPerformance(anyString());
  }



}
