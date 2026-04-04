//package com.fortytwogroup.controller;
//
//import com.fortytwogroup.model.Booking;
//import com.fortytwogroup.model.Performance;
//import com.fortytwogroup.model.Student;
//import com.fortytwogroup.model.enums.BookingStatus;
//import com.fortytwogroup.model.enums.PerformanceStatus;
//import com.fortytwogroup.MockPaymentSystem;
//import com.fortytwogroup.MockVerificationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BookingController#bookPerformance()}.
 *
 * Each test method covers one specific input scenario. Tests are named
 * descriptively so the case under test is clear at a glance.
 *
 * Assumptions (hardcoded for testing, per CW3 spec):
 *   - A pre-registered student account is available.
 *   - A ticketed performance with known capacity exists.
 *   - MockPaymentSystem is used in place of a real payment service.
 */

/*
class TestBookingController {

    // ---------------------------------------------------------------
    // Shared fixtures — rebuilt fresh before each test via @BeforeEach
    // ---------------------------------------------------------------

    private BookingController bookingController;
    private MockPaymentSystem paymentSystem;

    /** A ticketed performance that has NOT yet happened, with 10 seats. */
    /* private Performance ticketedPerformance;

    /** A non-ticketed (free) performance. */
    /*private Performance nonTicketedPerformance;

    /** A ticketed performance that is ALREADY CANCELLED. */
 //   private Performance cancelledPerformance;
//
    ///** A ticketed performance that has already happened (in the past). */
  //  private Performance pastPerformance;

    ///** A ticketed performance with 0 tickets remaining. */
    //private Performance soldOutPerformance;

    ///** A pre-registered student used as the current logged-in user. */
    //private Student student;
//
   //  @BeforeEach
    //void setUp() {
      //  paymentSystem = new MockPaymentSystem();
        //bookingController = new BookingController(paymentSystem);

        //// Pre-registered student (hardcoded per spec)
        //student = new Student("alice@uni.ac.uk", "Alice", 447700900000L);

        // Ticketed future performance — happy path
        //ticketedPerformance = new Performance(
          //      1L,
            //    LocalDateTime.now().plusDays(7),   // starts in the future
              //  LocalDateTime.now().plusDays(7).plusHours(2),
                //List.of("Band X"),
                //"123 Main St",
                //100,   // venue capacity
                //false, // not outdoors
                //false, // no smoking
                //50,    // total tickets
                //10,    // tickets sold — 40 remaining
                //15.00, // ticket price
                //false, // not sponsored
                //0.0,
                //PerformanceStatus.ACTIVE
        //);

        // Non-ticketed performance (free entry, no booking needed)
        //nonTicketedPerformance = new Performance(
          //      2L,
            //    LocalDateTime.now().plusDays(3),
              //  LocalDateTime.now().plusDays(3).plusHours(1),
                //List.of("DJ Y"),
                //"456 Other St",
                //200,
                //true, false,
                //0,    // 0 tickets means non-ticketed
                //0, 0.0, false, 0.0,
                //PerformanceStatus.ACTIVE
        //);

        // Cancelled performance
        //cancelledPerformance = new Performance(
         //       3L,
           //     LocalDateTime.now().plusDays(5),
             //   LocalDateTime.now().plusDays(5).plusHours(2),
               // List.of("Singer Z"),
                //"789 Venue Ave",
                //80, false, false,
               // 40, 5, 20.00, false, 0.0,
                //PerformanceStatus.CANCELLED   // already cancelled
        //);
//
   //      // Past performance — should not be bookable
     //   pastPerformance = new Performance(
       //         4L,
         //       LocalDateTime.now().minusDays(1),  // yesterday
           //     LocalDateTime.now().minusDays(1).plusHours(2),
             //   List.of("Old Act"),
               // "History Hall",
                //50, false, false,
                //30, 10, 10.00, false, 0.0,
                //PerformanceStatus.ACTIVE
       // );

        // Sold-out performance
        //soldOutPerformance = new Performance(
          //      5L,
            //    LocalDateTime.now().plusDays(2),
              //  LocalDateTime.now().plusDays(2).plusHours(2),
                //List.of("Hot Act"),
                //"Packed Venue",
                //60, false, false,
                //60, 60, 25.00, false, 0.0,  // numTicketsSold == numTicketsTotal
                //PerformanceStatus.ACTIVE
        //);
//
   //      // Set the student as current user in the controller
     //   bookingController.setCurrentUser(student);
    //}

    // ---------------------------------------------------------------
    // Main Success Scenario
    // ---------------------------------------------------------------

  //  /**
//     * Happy path: student books 2 tickets for a valid ticketed future performance.
    // * Expects a booking to be created with ACTIVE status.
    // */
    //@Test
    //void bookPerformance_validTicketedPerformance_bookingCreated() {
     //   Booking result = bookingController.bookPerformance(ticketedPerformance.getPerformanceId(), 2);
//
   //      assertNotNull(result, "A booking should have been created for a valid ticketed performance");
     //   assertEquals(BookingStatus.ACTIVE, result.getStatus(),
       //         "Newly created booking should have ACTIVE status");
    //}
//
  //   /**
    // * Happy path: booking exactly 1 ticket (minimum boundary value).
     //*/
    //@Test
 //  //void bookPerformance_oneTicket_bookingCreated() {
 //    //  Booking result = bookingController.bookPerformance(ticketedPerformance.getPerformanceId(), 1);

 //      assertNotNull(result, "Booking 1 ticket should succeed");
 //  }

 //  /**
 //   * Happy path: booking all remaining tickets at once (upper boundary).
 //   * ticketedPerformance has 40 tickets remaining.
 //   */
 //  @Test
 //  void bookPerformance_allRemainingTickets_bookingCreated() {
 //      int remaining = ticketedPerformance.getNumTicketsTotal() - ticketedPerformance.getNumTicketsSold();
 //      Booking result = bookingController.bookPerformance(ticketedPerformance.getPerformanceId(), remaining);

 //      assertNotNull(result, "Booking all remaining tickets should succeed");
 //  }

 //  // ---------------------------------------------------------------
 //  // Extension 1a — invalid / non-existent performance ID
 //  // ---------------------------------------------------------------

 //  /**
 //   * Extension 1a: performance ID does not exist in the system.
 //   * Expects null (or an appropriate failure indicator) to be returned.
 //   */
 //  @Test
 //  void bookPerformance_nonExistentPerformanceId_returnsNull() {
 //      Booking result = bookingController.bookPerformance(999L, 1);

 //      assertNull(result, "Booking with a non-existent performance ID should fail and return null");
 //  }

 //  // ---------------------------------------------------------------
 //  // Extension 1b — non-ticketed performance
 //  // ---------------------------------------------------------------

 //  /**
 //   * Extension 1b: student attempts to book a non-ticketed (free) performance.
 //   * Expects null because booking is not needed for free events.
 //   */
 //  @Test
 //  void bookPerformance_nonTicketedPerformance_returnsNull() {
 //      Booking result = bookingController.bookPerformance(nonTicketedPerformance.getPerformanceId(), 1);

 //      assertNull(result,
 //              "Booking a non-ticketed performance should fail — no booking needed for free events");
 //  }

 //  // ---------------------------------------------------------------
 //  // Extension 1c — cancelled performance
 //  // ---------------------------------------------------------------

 //  /**
 //   * Extension 1c: student tries to book a performance that is already cancelled.
 //   * Expects null.
 //   */
 //  @Test
 //  void bookPerformance_cancelledPerformance_returnsNull() {
 //      Booking result = bookingController.bookPerformance(cancelledPerformance.getPerformanceId(), 1);

 //      assertNull(result, "Booking a cancelled performance should fail and return null");
 //  }

 //  // ---------------------------------------------------------------
 //  // Extension — past performance
 //  // ---------------------------------------------------------------

 //  /**
 //   * Student attempts to book a performance that has already taken place.
 //   * Expects null.
 //   */
 //  @Test
 //  void bookPerformance_pastPerformance_returnsNull() {
 //      Booking result = bookingController.bookPerformance(pastPerformance.getPerformanceId(), 1);

 //      assertNull(result, "Booking a past performance should fail and return null");
 //  }

 //  // ---------------------------------------------------------------
 //  // Extension 3a — not enough tickets
 //  // ---------------------------------------------------------------

 //  /**
 //   * Extension 3a: requested ticket count exceeds available supply.
 //   */
 //  @Test
 //  void bookPerformance_requestMoreTicketsThanAvailable_returnsNull() {
 //      int tooMany = (ticketedPerformance.getNumTicketsTotal()
 //              - ticketedPerformance.getNumTicketsSold()) + 1; // one over what's left

 //      Booking result = bookingController.bookPerformance(ticketedPerformance.getPerformanceId(), tooMany);

 //      assertNull(result, "Requesting more tickets than available should fail and return null");
 //  }

 //  /**
 //   * Extension 3a: performance is completely sold out.
 //   */
 //  @Test
 //  void bookPerformance_soldOutPerformance_returnsNull() {
 //      Booking result = bookingController.bookPerformance(soldOutPerformance.getPerformanceId(), 1);

 //      assertNull(result, "Booking a sold-out performance should fail and return null");
 //  }

 //  // ---------------------------------------------------------------
 //  // Extension — invalid ticket count inputs
 //  // ---------------------------------------------------------------

 //  /**
 //   * Boundary: requesting 0 tickets is invalid.
 //   */
 //  @Test
 //  void bookPerformance_zeroTickets_returnsNull() {
 //      Booking result = bookingController.bookPerformance(ticketedPerformance.getPerformanceId(), 0);

 //      assertNull(result, "Requesting 0 tickets should be rejected and return null");
 //  }

 //  /**
 //   * Boundary: requesting a negative number of tickets is invalid.
 //   */
 //  @Test
 //  void bookPerformance_negativeTickets_returnsNull() {
 //      Booking result = bookingController.bookPerformance(ticketedPerformance.getPerformanceId(), -3);

 //      assertNull(result, "A negative ticket count should be rejected and return null");
 //  }

 //  // ---------------------------------------------------------------
 //  // Extension 4a — payment failure
 //  // ---------------------------------------------------------------

 //  /**
 //   * Extension 4a: payment system rejects the transaction.
 //   * Uses a BookingController wired with a payment system that always fails.
 //   * Expects the booking status to be PAYMENTFAILED (not ACTIVE).
 //   */
 //  @Test
 //  void bookPerformance_paymentFails_bookingStatusIsPaymentFailed() {
 //      // Wire in a payment system that always returns false
 //      BookingController controllerWithFailingPayment =
 //              new BookingController(new AlwaysFailingPaymentSystem());
 //      controllerWithFailingPayment.setCurrentUser(student);

 //      Booking result = controllerWithFailingPayment.bookPerformance(
 //              ticketedPerformance.getPerformanceId(), 2);

 //      // Either null (no booking persisted) or PAYMENTFAILED — both acceptable
 //      if (result != null) {
 //          assertEquals(BookingStatus.PAYMENTFAILED, result.getStatus(),
 //                  "When payment fails, booking status should be PAYMENTFAILED");
 //      } else {
 //          assertNull(result, "When payment fails, no booking should be created");
 //      }
 //  }

 //  // ---------------------------------------------------------------
 //  // Helper — stub payment system that always fails
 //  // ---------------------------------------------------------------

 //  /** Minimal PaymentSystem stub that always returns false. */
 //  private static class AlwaysFailingPaymentSystem implements external.PaymentSystem {
 //      @Override
 //      public boolean processPayment(int numTickets, String eventTitle, String studentEmail,
 //                                    int studentPhone, String epEmail, double transactionAmount) {
 //          return false; // simulate payment decline
 //      }

 //      @Override
 //      public boolean processRefund(int numTickets, String eventTitle, String studentEmail,
 //                                   int studentPhone, String epEmail, double transactionAmount,
 //                                   String organiserMsg) {
 //          return false;
 //      }
 //  }
//}
