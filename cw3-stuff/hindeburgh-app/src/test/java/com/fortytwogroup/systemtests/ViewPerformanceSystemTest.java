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

public class ViewPerformanceSystemTest {

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
  void testViewPerformance_idNotOnSystemGivesErrorMessage() {
    // no performances added to system, so any ID will not be found
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("999");

    eventPerformanceController.viewPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("No performance found with that ID");
  }

  @Test
  void testViewPerformance_idIsOnSystemDisplaysCorrectPerformance() {
    // create an EP, log in and create an event with a performance
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
            "1"  // view performance with ID 1
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    eventPerformanceController.viewPerformance();

    Mockito.verify(mockTextUserInterface)
        .displaySpecificPerformance(anyString());
  }

  @Test
  void testViewPerformance_rejectsNonIntegerPerformanceIDInput() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("abc");

    eventPerformanceController.viewPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("ID must be a positive integer");
  }

  @Test
  void testViewPerformance_returnsCorrectPerformanceForGivenID() {
    // create two performances so we can check the right one is returned
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            // first performance
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            // second performance
            "yes",
            "2026-07-01", "10:00", "2026-07-01", "11:00",
            "yes", "50", "5.00",
            "Enter performers", "Jane", "no_more_performers",
            "Enter venue", "outdoors", "yes", "456 Street", "200",
            "no",
            "2"  // view second performance specifically
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    eventPerformanceController.viewPerformance();

    // verify the displayed performance contains details specific to the second performance
    Mockito.verify(mockTextUserInterface)
        .displaySpecificPerformance(Mockito.argThat(s -> s.contains("456 Street")));
  }

  @Test
  void testViewPerformance_earlyTerminationOfMethodOnInvalidPerformanceIDFormat() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("abc");

    eventPerformanceController.viewPerformance();

    // verify early return was hit
    Mockito.verify(mockTextUserInterface, Mockito.never())
        .displaySpecificPerformance(anyString());
  }

}
