package com.fortytwogroup.systemtests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.view.TextUserInterface;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ReviewPerformanceSystemTest {
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
  void testReviewPerformance_cantReviewPerformanceThatHasntHappenedYet() {
    // create a performance in the future
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2030-06-01", "10:00", "2030-06-01", "11:00",  // future date
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // log in as student and try to review
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345", "1");
    userController.login();
    bookingController.reviewPerformance();

    // should silently do nothing - no success and no error about the review itself
    Mockito.verify(mockTextUserInterface, Mockito.never())
        .displaySuccess("Review submitted!");
  }



  @Test
  void testReviewPerformance_rejectsNullComments() {
    // create a past performance
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2020-06-01", "10:00", "2020-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // submit review with blank comment
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345", "1", "4", "");
    userController.login();
    bookingController.reviewPerformance();

    // blank comment should not be added to review comments
    Performance p = eventPerformanceController.getPerformances().iterator().next();
    assertTrue(p.getReviewComments().isEmpty(),
        "Blank comment should not be added to review comments");
  }

  @Test
  void testReviewPerformance_rejectsEmptyComments() {
    // same as above - empty string should not be stored
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2020-06-01", "10:00", "2020-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345", "1", "4", "   ");
    userController.login();
    bookingController.reviewPerformance();

    Performance p = eventPerformanceController.getPerformances().iterator().next();
    assertTrue(p.getReviewComments().isEmpty(),
        "Whitespace-only comment should not be added to review comments");
  }

  @Test
  void testReviewPerformance_ratingsBetween1And5() {
    // create a past performance
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn(
            "OrgName", "BN123", "ep@test.com", "eppass", "John", "A description",
            "ep@test.com", "eppass",
            "My Event", "MUSIC", "yes",
            "yes",
            "2020-06-01", "10:00", "2020-06-01", "11:00",
            "yes", "100", "10.00",
            "Enter performers", "John", "no_more_performers",
            "Enter venue", "indoors", "no", "123 Street", "500",
            "no"
        );
    userController.registerEntertainmentProvider();
    userController.login();
    eventPerformanceController.createEvent();

    // submit review with rating 6 - out of range
    when(mockTextUserInterface.getInput(anyString()))
        .thenReturn("chrisbarnes@school.com", "cb12345", "1", "6", "Great show!");
    userController.login();
    bookingController.reviewPerformance();

    // rating 6 should not have been added since it's out of range
    Performance p = eventPerformanceController.getPerformances().iterator().next();
    assertTrue(p.getReviewRatings().isEmpty(),
        "Rating of 6 should be rejected as it is outside the 1-5 range");
  }
}