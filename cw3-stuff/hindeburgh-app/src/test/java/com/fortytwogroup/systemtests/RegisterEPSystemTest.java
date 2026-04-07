package com.fortytwogroup.systemtests;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.MenuController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.model.*;
import com.fortytwogroup.model.enums.BookingStatus;
import com.fortytwogroup.view.TextUserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterEPSystemTest {

    private UserController userController;
    private MenuController menuController;
    private BookingController mockBookingController;
    private TextUserInterface mockTextUserInterface;
    private MockVerificationService mockVerificationService;
    private EventPerformanceController mockEventPerformanceController;

    @BeforeEach
    public void setUp() {
        mockBookingController = mock(BookingController.class);
        mockTextUserInterface = mock(TextUserInterface.class);
        mockVerificationService = mock(MockVerificationService.class);
        mockEventPerformanceController = mock(EventPerformanceController.class);
        userController = new UserController(
                mockTextUserInterface,
                mockVerificationService
        );
        menuController = new MenuController(
                userController,
                mockEventPerformanceController,
                mockBookingController,
                mockTextUserInterface
        );
    }

    @Test
    void registerEntertainmentProvider_successful() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("register_ep");
        when(mockTextUserInterface.getInput("Organisation Name: ")).thenReturn("Eventful Events");
        when(mockTextUserInterface.getInput("Business Number: ")).thenReturn("6372646378");
        when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("eventfulEvents@business.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("Events123");
        when(mockTextUserInterface.getInput("Name: ")).thenReturn("Evan Evans");
        when(mockTextUserInterface.getInput("Description: ")).thenReturn(
                "desc"
        );

        menuController.mainMenu();

        verify(mockTextUserInterface).displaySuccess("Successfully registered new EP!");

        assertNotNull(
                userController.getUsers().get("eventfulEvents@business.com"),
                "After successfully registering an EP, it should be stored in the map of users"
        );
    }

    @Test
    void registerEntertainmentProvider_empty_field() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("register_ep");
        //Staggering the blank values to check error is thrown for all empty fields
        when(mockTextUserInterface.getInput("Organisation Name: ")).thenReturn(
                "", "Eventful Events"
        );
        when(mockTextUserInterface.getInput("Business Number: ")).thenReturn(
                "6372646378", "", "6372646378"
        );
        when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
        when(mockTextUserInterface.getInput("Email: ")).thenReturn(
                "eevents@business.com", "eevents@business.com", "", "eevents@business.com"
        );
        when(mockTextUserInterface.getInput("Password: ")).thenReturn(
                "Events123", "Events123", "Events123", "", "Events123"
        );
        when(mockTextUserInterface.getInput("Name: ")).thenReturn(
                "Evan Evans", "Evan Evans", "Evan Evans", "Evan Evans", "", "Evan Evans"
        );
        when(mockTextUserInterface.getInput("Description: ")).thenReturn(
                "desc", "desc", "desc", "desc", "desc", "", "desc"
        );

        menuController.mainMenu();

        verify(mockTextUserInterface, times(6)).displayError("A field cannot be blank!");

        verify(mockTextUserInterface).displaySuccess("Successfully registered new EP!");

        assertNotNull(
                userController.getUsers().get("eevents@business.com"),
                "After successfully registering an EP, it should be stored in the map of users"
        );
    }

    @Test
    void registerEntertainmentProvider_invalid_businessNumber() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("register_ep");
        when(mockTextUserInterface.getInput("Organisation Name: ")).thenReturn("Eventful Events", "Eventful Events");
        when(mockTextUserInterface.getInput("Business Number: ")).thenReturn("1", "6372646378");
        when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(false, true);
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("eventfulEvents@business.com", "eventfulEvents@business.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("Events123", "Events123");
        when(mockTextUserInterface.getInput("Name: ")).thenReturn("Evan Evans", "Evan Evans");
        when(mockTextUserInterface.getInput("Description: ")).thenReturn(
                "desc", "desc"
        );

        menuController.mainMenu();

        verify(mockTextUserInterface).displayError("Verification failed!");

        verify(mockTextUserInterface).displaySuccess("Successfully registered new EP!");

        assertNotNull(
                userController.getUsers().get("eventfulEvents@business.com"),
                "After successfully registering an EP, it should be stored in the map of users"
        );
    }

    @Test
    void registerEntertainmentProvider_ep_already_registered() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("register_ep");
        when(mockTextUserInterface.getInput("Organisation Name: ")).thenReturn(
                "Eventful Events", "Eventful Events", "Popping Parties"
        );
        when(mockTextUserInterface.getInput("Business Number: ")).thenReturn(
                "6372646378", "6372646378", "7483948914"
        );
        when(mockVerificationService.verifyEntertainmentProvider(anyString())).thenReturn(true);
        when(mockTextUserInterface.getInput("Email: ")).thenReturn(
                "eventfulEvents@business.com", "eventfulEvents@business.com", "pparties@business.com"
        );
        when(mockTextUserInterface.getInput("Password: ")).thenReturn(
                "Events123", "Events123", "parties"
        );
        when(mockTextUserInterface.getInput("Name: ")).thenReturn(
                "Evan Evans", "Evan Evans", "Paul Porter"
        );
        when(mockTextUserInterface.getInput("Description: ")).thenReturn(
                "desc"
        );

        menuController.mainMenu();

        verify(mockTextUserInterface).displaySuccess("Successfully registered new EP!");

        assertNotNull(
                userController.getUsers().get("eventfulEvents@business.com"),
                "After successfully registering an EP, it should be stored in the map of users"
        );

        menuController.mainMenu();

        verify(mockTextUserInterface).displayError("This EP is already registered!");
    }


}
