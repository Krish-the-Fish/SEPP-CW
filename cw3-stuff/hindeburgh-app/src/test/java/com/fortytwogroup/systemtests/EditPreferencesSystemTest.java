package com.fortytwogroup.systemtests;

import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.MenuController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockVerificationService;

import com.fortytwogroup.model.Student;
import com.fortytwogroup.view.TextUserInterface;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class EditPreferencesSystemTest {

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
    public void testEditPreferences_Success() {
        Student student = new Student("John Doe", 479826534, "johndoe@email.com", "password");
        userController.setCurrentUser(student);

        when(mockTextUserInterface.getInput("Preferences: ")).thenReturn("music, dance, theatre");

        userController.editPreferences();

        verify(mockTextUserInterface).displaySuccess("Preferences updated!");

        verify(mockTextUserInterface, never()).displayError(anyString());

        assertTrue(student.getPreferenceClass().preferMusicEvents);
        assertTrue(student.getPreferenceClass().preferDanceEvents);
        assertTrue(student.getPreferenceClass().preferTheatreEvents);
        assertFalse(student.getPreferenceClass().preferSportsEvents);
        assertFalse(student.getPreferenceClass().preferMovieEvents);
    }

    @Test
    public void testEditPreferences_invalid_preference() {
        Student student = new Student("John Doe", 479826534, "johndoe@email.com", "password");
        userController.setCurrentUser(student);

        when(mockTextUserInterface.getInput("Preferences: ")).thenReturn(
                "music, dance, INVALID", "music, dance, theatre"
        );

        userController.editPreferences();

        verify(mockTextUserInterface).displayError("INVALID is not a valid preference!");

        verify(mockTextUserInterface).displaySuccess("Preferences updated!");

        assertTrue(student.getPreferenceClass().preferMusicEvents);
        assertTrue(student.getPreferenceClass().preferDanceEvents);
        assertTrue(student.getPreferenceClass().preferTheatreEvents);
        assertFalse(student.getPreferenceClass().preferSportsEvents);
        assertFalse(student.getPreferenceClass().preferMovieEvents);
    }

    @Test
    public void testEditPreferences_multiple_invalids() {
        Student student = new Student("John Doe", 479826534, "johndoe@email.com", "password");
        userController.setCurrentUser(student);

        when(mockTextUserInterface.getInput("Preferences: ")).thenReturn(
                "INVALID, INVALID, INVALID", "music, dance, theatre"
        );

        userController.editPreferences();

        verify(mockTextUserInterface, times(3)).displayError("INVALID is not a valid preference!");

        verify(mockTextUserInterface).displaySuccess("Preferences updated!");

        assertTrue(student.getPreferenceClass().preferMusicEvents);
        assertTrue(student.getPreferenceClass().preferDanceEvents);
        assertTrue(student.getPreferenceClass().preferTheatreEvents);
        assertFalse(student.getPreferenceClass().preferSportsEvents);
        assertFalse(student.getPreferenceClass().preferMovieEvents);
    }

    @Test
    public void testEditPreferences_too_many_selections() {
        Student student = new Student("John Doe", 479826534, "johndoe@email.com", "password");
        userController.setCurrentUser(student);

        when(mockTextUserInterface.getInput("Preferences: ")).thenReturn(
                "music, dance, theatre, sports", "music, dance, theatre"
        );

        userController.editPreferences();

        verify(mockTextUserInterface).displayError("Too many preferences!");

        verify(mockTextUserInterface).displaySuccess("Preferences updated!");

        assertTrue(student.getPreferenceClass().preferMusicEvents);
        assertTrue(student.getPreferenceClass().preferDanceEvents);
        assertTrue(student.getPreferenceClass().preferTheatreEvents);
        assertFalse(student.getPreferenceClass().preferSportsEvents);
        assertFalse(student.getPreferenceClass().preferMovieEvents);
    }
}
