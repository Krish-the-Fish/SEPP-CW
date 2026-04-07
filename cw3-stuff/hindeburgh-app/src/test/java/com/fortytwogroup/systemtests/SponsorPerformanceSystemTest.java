package com.fortytwogroup.systemtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.MenuController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.view.TextUserInterface;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SponsorPerformanceSystemTest {
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
  void testSponsorPerformance_handleInvalidPerformanceID() {
    // create a ticketed performance first
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

    // log in as admin and try invalid performance ID
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "joeblogs@school.com", "password123",
            "999", "5.00",  // invalid ID
            "1", "5.00"     // valid ID on retry
        );
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Performance with given number does not exist");
  }

  @Test
  void testSponsorPerformance_rejectExtremelyHighSponsorshipAmount() {
    // create a ticketed performance first
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

    // log in as admin and try amount larger than ticket price
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "joeblogs@school.com", "password123",
            "1", "99999999.00",  // way above ticket price of 10.00
            "1", "5.00"          // valid on retry
        );
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("The amount provided is invalid.");
  }

  @Test
  void testSponsorPerformance_rejectNegativeSponsorAmount() {
    // create a ticketed performance first
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "joeblogs@school.com", "password123",
            "1", "-5.00",  // negative sponsorship amount
            "1", "5.00"    // valid on retry
        );
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("The amount provided is invalid.");
  }

  @Test
  void testSponsorPerformance_onlyAdminStaffCanSponsor() {
    // log in as student and try to sponsor
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345");
    userController.login();

    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Only admins can sponsor a performance. ");
  }

  @Test
  void testSponsorPerformance_rejectNotTwoDecimalPlaceSponsorAmount() {
    // create a ticketed performance first
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "joeblogs@school.com", "password123",
            "1", "5.123",  // more than 2 decimal places
            "1", "5.00"    // valid on retry
        );
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Sponsorship amount cannot have more than 2 decimal places");
  }

  @Test
  void testSponsorPerformance_successMessageForValidInputDetails() {
    // create a ticketed performance first
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("joeblogs@school.com", "password123", "1", "5.00");
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Sponsorship Successful!");
  }

  @Test
  void testSponsorPerformance_sponsorshipAmountVariableUpdated() {
    // create a ticketed performance first
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("joeblogs@school.com", "password123", "1", "5.00");
    userController.login();
    eventPerformanceController.sponsorPerformance();

    // get the performance and check sponsorship amount was updated
    Performance p = eventPerformanceController.getPerformances().iterator().next();
    assertEquals(5.00, p.getSponsorshipAmountRemaining(),
        "Sponsorship amount remaining should be updated after sponsorship");
  }

  @Test
  void testSponsorPerformance_sponsorshipStatusVariableUpdated() {
    // create a ticketed performance first
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("joeblogs@school.com", "password123", "1", "5.00");
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Performance p = eventPerformanceController.getPerformances().iterator().next();
    assertTrue(p.getIsSponsored(),
        "isSponsored should be true after sponsorship");
  }

  @Test
  void testSponsorPerformance_askAgainUponInvalidPerformanceID() {
    // create a ticketed performance first
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "joeblogs@school.com", "password123",
            "999", "5.00",  // invalid ID
            "1", "5.00"     // valid on retry
        );
    userController.login();
    eventPerformanceController.sponsorPerformance();

    // if it asked again, sponsorship should have succeeded
    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Sponsorship Successful!");
  }

  @Test
  void testSponsorPerformance_askAgainUponInvalidSponsorshipAmount() {
    // create a ticketed performance first
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "joeblogs@school.com", "password123",
            "1", "-5.00",  // invalid amount
            "1", "5.00"    // valid on retry
        );
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displaySuccess("Sponsorship Successful!");
  }

  @Test
  void testSponsorPerformance_rejectSponsorAmountGreaterThanTicketPrice() {
    // create a ticketed performance with ticket price 10.00
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

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "joeblogs@school.com", "password123",
            "1", "11.00",  // should fail first time
            "1", "5.00"    // valid on retry
        );
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("The amount provided is invalid.");
  }

  @Test
  void testSponsorPerformance_cantSponsorNonTicketedPerformance() {
    // create a non-ticketed performance
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "no",  // not ticketed
            "yes",
            "2026-06-01", "10:00", "2026-06-01", "11:00",
            "no",  // performance not ticketed
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("joeblogs@school.com", "password123", "1", "5.00");
    userController.login();
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("The requested performance's event is not ticketed. It cannot be sponsored.");
  }

  @Test
  void testSponsorPerformance_epCannotSponsor() {
    // register and log in as EP
    when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass"
        );
    userController.registerEntertainmentProvider();
    userController.login();

    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Only admins can sponsor a performance. ");
  }

  @Test
  void testSponsorPerformance_studentCannotSponsor() {
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345");
    userController.login();

    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Only admins can sponsor a performance. ");
  }

  @Test
  void testSponsorPerformance_notLoggedInCannotSponsor() {
    // no login - currentUser is null
    eventPerformanceController.sponsorPerformance();

    Mockito.verify(mockTextUserInterface)
        .displayError("Only admins can sponsor a performance. ");
  }

}
