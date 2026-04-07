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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class LoginSystemTests {

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
    public void login_Success() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("login");
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("12345");
        menuController.mainMenu();

        verify(mockTextUserInterface).displaySuccess("Successfully logged in!");

        assertEquals(
                userController.getUsers().get("kevinking@school.com"),
                userController.getCurrentUser(),
                "Loging in should assign the correct account to the current user variable."
        );
    }

    @Test
    void login_empty_email() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("login");
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("", "kevinking@school.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("12345", "12345");

        menuController.mainMenu();

        verify(mockTextUserInterface).displayError("Email cannot be blank!");

        verify(mockTextUserInterface).displaySuccess("Successfully logged in!");

        assertEquals(
                userController.getUsers().get("kevinking@school.com"),
                userController.getCurrentUser(),
                "After failing login due to empty email, a successful login should be allowed"
        );
    }

    @Test
    void login_empty_password() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("login");
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com", "kevinking@school.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("", "12345");

        userController.login();

        verify(mockTextUserInterface).displayError("Password cannot be blank!");

        verify(mockTextUserInterface).displaySuccess("Successfully logged in!");

        assertEquals(
                userController.getUsers().get("kevinking@school.com"),
                userController.getCurrentUser(),
                "After failing login due to empty password, a successful login should be allowed"
        );
    }

    @Test
    void login_non_existing_email() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("login");
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("nonexisting@school.com", "kevinking@school.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("", "12345");

        userController.login();

        verify(mockTextUserInterface).displayError("User not found!");

        verify(mockTextUserInterface).displaySuccess("Successfully logged in!");

        assertEquals(
                userController.getUsers().get("kevinking@school.com"),
                userController.getCurrentUser(),
                "After failing login due to empty password, a successful login should be allowed"
        );
    }

    @Test
    void login_incorrect_password() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("login");
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("kevinking@school.com", "kevinking@school.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("incorrectpassword", "12345");

        userController.login();

        verify(mockTextUserInterface).displayError("Incorrect password!");

        verify(mockTextUserInterface).displaySuccess("Successfully logged in!");

        assertEquals(
                userController.getUsers().get("kevinking@school.com"),
                userController.getCurrentUser(),
                "After failing login due to empty password, a successful login should be allowed"
        );
    }
}
