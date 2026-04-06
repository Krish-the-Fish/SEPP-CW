package com.fortytwogroup.systemtests;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.MenuController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.model.Performance;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.view.TextUserInterface;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.external.PaymentSystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;

public class BookPerformanceSystemTest {

    private TextUserInterface mockUI;
    private MockPaymentSystem mockPaymentSystem;
    private MockVerificationService mockVerificationService;
    private Collection<Performance> performances;

    private MenuController menuController;
    private UserController userController;
    private EventPerformanceController eventPerformanceController;
    private BookingController bookingController;

    // pre-registered student (hardcoded as per spec)
    private static final String STUDENT_NAME = "Alice Smith";
    private static final String STUDENT_EMAIL = "alice@student.ed.ac.uk";
    private static final String STUDENT_PASSWORD = "studentPass1";
    private static final int STUDENT_PHONE = 7123456;

    // EP details for setup
    private static final String EP_ORG = "Live Music Ltd";
    private static final String EP_BN = "BN123456";
    private static final String EP_NAME = "Bob Jones";
    private static final String EP_DESC = "A live music provider";
    private static final String EP_EMAIL = "bob@livemusic.com";
    private static final String EP_PASS = "epPass1";

    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());

        mockUI = mock(TextUserInterface.class);
        mockPaymentSystem = new MockPaymentSystem();
        mockVerificationService = mock(MockVerificationService.class);
        when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
        performances = new ArrayList<>();

        userController = new UserController(mockUI, mockVerificationService);
        eventPerformanceController = new EventPerformanceController(mockUI, performances, mockPaymentSystem);
        bookingController = new BookingController(performances, mockUI, mockPaymentSystem);
        menuController = new MenuController(
                userController, eventPerformanceController, bookingController, mockUI);

        // hardcode the pre-registered student into the system
        Student student = new Student(STUDENT_NAME, STUDENT_PHONE, STUDENT_EMAIL, STUDENT_PASSWORD);
        userController.getUsers().put(STUDENT_EMAIL, student);
    }

    @AfterEach
    void tearDown() {
        System.out.println("---");
    }

    // setup helpers

    private void registerEP() {
        when(mockUI.getInput("Organisation Name: ")).thenReturn(EP_ORG);
        when(mockUI.getInput("Business Number: ")).thenReturn(EP_BN);
        when(mockUI.getInput("Email: ")).thenReturn(EP_EMAIL);
        when(mockUI.getInput("Password: ")).thenReturn(EP_PASS);
        when(mockUI.getInput("Name: ")).thenReturn(EP_NAME);
        when(mockUI.getInput("Description: ")).thenReturn(EP_DESC);
        userController.registerEntertainmentProvider();
    }

    private void loginEP() {
        when(mockUI.getInput("Email: ")).thenReturn(EP_EMAIL);
        when(mockUI.getInput("Password: ")).thenReturn(EP_PASS);
        userController.login();

        // Sync the logged-in user to the other controllers
        eventPerformanceController.setCurrentUser(userController.getCurrentUser());
        bookingController.setCurrentUser(userController.getCurrentUser());
    }

    private void loginStudent() {
        when(mockUI.getInput("Email: ")).thenReturn(STUDENT_EMAIL);
        when(mockUI.getInput("Password: ")).thenReturn(STUDENT_PASSWORD);
        userController.login();

        // Sync the logged-in user to the other controllers
        eventPerformanceController.setCurrentUser(userController.getCurrentUser());
        bookingController.setCurrentUser(userController.getCurrentUser());
    }

    // mocks all the prompts createEvent needs for a ticketed event with one performance
    private void epCreatesTicketedEvent(int numTickets, double ticketPrice) {
        when(mockUI.getInput("Enter event title: ")).thenReturn("Test Concert");
        when(mockUI.getInput("Enter event type: ")).thenReturn("MUSIC");
        when(mockUI.getInput("Is the event ticketed ('yes'/'no'): ")).thenReturn("yes");
        when(mockUI.getInput("Do you wish to add another performance (yes/no): "))
                .thenReturn("yes")
                .thenReturn("no");

        when(mockUI.getInput("Enter performance start date (YYYY-MM-DD): ")).thenReturn("2026-12-01");
        when(mockUI.getInput("Enter Performance start time (HH:MM): ")).thenReturn("19:00");
        when(mockUI.getInput("Enter performance end date (YYYY-MM-DD): ")).thenReturn("2026-12-01");
        when(mockUI.getInput("Enter Performance end time (HH:MM): ")).thenReturn("22:00");
        when(mockUI.getInput("Is the performance ticketed ('yes'/'no'): ")).thenReturn("yes");
        when(mockUI.getInput("Enter number of tickets available for the performance: "))
                .thenReturn(String.valueOf(numTickets));
        when(mockUI.getInput("Enter the ticket price for the performance: "))
                .thenReturn(String.valueOf(ticketPrice));

        when(mockUI.getInput("Enter the performer names for the performances."
                + "enter 'no_more_performers' when done")).thenReturn("");
        when(mockUI.getInput("Enter performer name: "))
                .thenReturn("Test Artist")
                .thenReturn("no_more_performers");
        when(mockUI.getInput("Enter the venue's details for the performances.")).thenReturn("");
        when(mockUI.getInput("Is the performance venue indoors or outdoors? ")).thenReturn("indoors");
        when(mockUI.getInput("Does the performance venue allow smoking? ")).thenReturn("no");
        when(mockUI.getInput("Please enter the performance venue address: ")).thenReturn("10 Main Street");
        when(mockUI.getInput("Please enter the performance venue capacity: ")).thenReturn("500");

        eventPerformanceController.createEvent();
    }

    private void epCreatesNonTicketedEvent() {
        when(mockUI.getInput("Enter event title: ")).thenReturn("Free Show");
        when(mockUI.getInput("Enter event type: ")).thenReturn("MUSIC");
        when(mockUI.getInput("Is the event ticketed ('yes'/'no'): ")).thenReturn("no");
        when(mockUI.getInput("Do you wish to add another performance (yes/no): "))
                .thenReturn("yes")
                .thenReturn("no");

        when(mockUI.getInput("Enter performance start date (YYYY-MM-DD): ")).thenReturn("2026-12-01");
        when(mockUI.getInput("Enter Performance start time (HH:MM): ")).thenReturn("19:00");
        when(mockUI.getInput("Enter performance end date (YYYY-MM-DD): ")).thenReturn("2026-12-01");
        when(mockUI.getInput("Enter Performance end time (HH:MM): ")).thenReturn("22:00");
        when(mockUI.getInput("Is the performance ticketed ('yes'/'no'): ")).thenReturn("no");

        when(mockUI.getInput("Enter the performer names for the performances." + "enter 'no_more_performers' when done")).thenReturn("");
        when(mockUI.getInput("Enter performer name: "))
                .thenReturn("Some Band")
                .thenReturn("no_more_performers");
        when(mockUI.getInput("Enter the venue's details for the performances.")).thenReturn("");
        when(mockUI.getInput("Is the performance venue indoors or outdoors? ")).thenReturn("indoors");
        when(mockUI.getInput("Does the performance venue allow smoking? ")).thenReturn("no");
        when(mockUI.getInput("Please enter the performance venue address: ")).thenReturn("10 Main Street");
        when(mockUI.getInput("Please enter the performance venue capacity: ")).thenReturn("500");

        eventPerformanceController.createEvent();
    }

    // full setup then clear so verify only counts bookPerformance calls
    private void fullSetup(int numTickets, double ticketPrice) {
        registerEP();
        loginEP();
        epCreatesTicketedEvent(numTickets, ticketPrice);
        userController.logout();
        loginStudent();
        clearInvocations(mockUI);
    }

    // book performance tests

    // main success scenario: valid ID, valid tickets, payment goes through
    @Test
    void bookPerformanceSuccessful() {
        fullSetup(100, 10.0);

        when(mockUI.getInput("Enter Performance ID ")).thenReturn("1");
        when(mockUI.getInput("Enter Number of Tickets ")).thenReturn("2");

        bookingController.bookPerformance();

        verify(mockUI, atLeastOnce()).displaySuccess(anyString());
        verify(mockUI, atLeastOnce()).displayBookingRecord(anyString());
    }

    // performance ID doesn't exist, system should show error and ask again
    @Test
    void bookPerformanceInvalidId() {
        fullSetup(100, 10.0);

        // give wrong ID first then correct one on retry
        when(mockUI.getInput("Enter Performance ID "))
                .thenReturn("9999")
                .thenReturn("1");
        when(mockUI.getInput("Enter Number of Tickets "))
                .thenReturn("2")
                .thenReturn("2");

        bookingController.bookPerformance();

        verify(mockUI, atLeastOnce()).displayError(anyString());
    }

    // performance belongs to a non-ticketed event, should say it's free
    @Test
    void bookPerformanceNonTicketedEvent() {
        registerEP();
        loginEP();
        epCreatesNonTicketedEvent();
        userController.logout();
        loginStudent();
        clearInvocations(mockUI);

        // Throw an exception on the 2nd attempt to break the infinite loop
        when(mockUI.getInput("Enter Performance ID "))
                .thenReturn("1")
                .thenThrow(new RuntimeException("Force exit loop"));
        when(mockUI.getInput("Enter Number of Tickets ")).thenReturn("1");

        try {
            bookingController.bookPerformance();
        } catch (RuntimeException e) {
            // Ignore the intentional exception used to break the loop
        }

        verify(mockUI, atLeastOnce()).displayError(anyString());
        verify(mockUI, never()).displayBookingRecord(anyString());
    }

    // asking for more tickets than are left
    @Test
    void bookPerformanceNotEnoughTickets() {
        fullSetup(5, 10.0);

        // Throw an exception on the 2nd attempt to break the infinite loop
        when(mockUI.getInput("Enter Performance ID "))
                .thenReturn("1")
                .thenThrow(new RuntimeException("Force exit loop"));
        when(mockUI.getInput("Enter Number of Tickets ")).thenReturn("10");

        try {
            bookingController.bookPerformance();
        } catch (RuntimeException e) {
            // Ignore the intentional exception used to break the loop
        }

        verify(mockUI, atLeastOnce()).displayError(anyString());
        verify(mockUI, never()).displayBookingRecord(anyString());
    }

    // payment fails, system should tell the student
    @Test
    void bookPerformancePaymentFails() {
        // need a payment system that returns false
        PaymentSystem failingPayment = mock(PaymentSystem.class);
        when(failingPayment.processPayment(
                anyInt(), anyString(), anyString(), anyInt(), anyString(), anyDouble()))
                .thenReturn(false);

        // rebuild booking controller with the failing payment system
        BookingController failBookingController = new BookingController(
                performances, mockUI, failingPayment);

        // still need the normal setup for EP + event
        registerEP();
        loginEP();
        epCreatesTicketedEvent(100, 10.0);
        userController.logout();
        loginStudent();
        clearInvocations(mockUI);

        when(mockUI.getInput("Enter Performance ID ")).thenReturn("1");
        when(mockUI.getInput("Enter Number of Tickets ")).thenReturn("2");

        failBookingController.bookPerformance();

        verify(mockUI, atLeastOnce()).displayError(anyString());
        verify(mockUI, never()).displayBookingRecord(anyString());
    }
}