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

public class LogoutSystemTest {
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
    public void logout_successful() {
        when(mockTextUserInterface.getInput("Command: ")).thenReturn("login", "logout");
        when(mockTextUserInterface.getInput("Email: ")).thenReturn("jjenkins@school.com");
        when(mockTextUserInterface.getInput("Password: ")).thenReturn("61212");

        menuController.mainMenu();

        verify(mockTextUserInterface).displaySuccess("Successfully logged in!");

        assertEquals(
                userController.getUsers().get("jjenkins@school.com"),
                userController.getCurrentUser()
        );

        assertInstanceOf(Student.class, menuController.getCurrentUser());
        assertInstanceOf(Student.class, userController.getCurrentUser());

        menuController.mainMenu();

        verify(mockTextUserInterface).displaySuccess("Successfully logged out!");

        assertNull(
                userController.getCurrentUser(),
                "Loging out should result in the current user being set to null."
        );
    }
}
