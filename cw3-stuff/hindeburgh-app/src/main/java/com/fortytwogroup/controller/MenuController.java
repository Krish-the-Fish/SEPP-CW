package com.fortytwogroup.controller;

import com.fortytwogroup.model.User;
import com.fortytwogroup.view.TextUserInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MenuController extends Controller {

  // declaring enums
  private enum AdminMenuOptions {
    LOGOUT,
    SEARCH_FOR_PERFORMANCES,
    VIEW_PERFORMANCE,
    SPONSOR_PERFORMANCE
  }

  private enum EPMenuOptions {
    LOGOUT,
    SEARCH_FOR_PERFORMANCES,
    VIEW_PERFORMANCE,
    CREATE_EVENT,
    CANCEL_PERFORMANCE
  }

  private enum GuestMenuOptions {
    LOGIN, REGISTER_EP
  }

  private enum StudentMenuOptions {
    LOGOUT,
    SEARCH_FOR_PERFORMANCES,
    VIEW_PERFORMANCE,
    REVIEW_PERFORMANCE,
    EDIT_PREFERENCES,
    BOOK_EVENT,
    CANCEL_BOOKING
  }

  private final UserController userController;
  private final EventPerformanceController eventPerformanceController;
  private final BookingController bookingController;
  private final TextUserInterface textUserInterface;

  public MenuController(
        UserController userController,
        EventPerformanceController eventPerformanceController,
        BookingController bookingController,
        TextUserInterface textUserInterface) {
    this.userController = userController;
    this.eventPerformanceController = eventPerformanceController;
    this.bookingController = bookingController;
    this.textUserInterface = textUserInterface;
    setTextUserInterface(textUserInterface);
  }

  public void mainMenu() {
    boolean validCommand = false;

    if (checkCurrentUserIsGuest()) {
      validCommand = handleGuestMainMenu();
    }
    else if (checkCurrentUserIsStudent()) {
      validCommand = handleStudentMainMenu();
    }
    else if (checkCurrentUserIsEntertainmentProvider()) {
      validCommand = handleEntertainmentProviderMainMenu();
    }
    else if (checkCurrentUserIsAdmin()) {
      validCommand = handleAdminStaffMainMenu();
    }

    if (!validCommand) {
      textUserInterface.displayError("Invalid command");
    }
  }

  private boolean handleGuestMainMenu(){

    int choice = selectFromMenu(
            Arrays.stream(GuestMenuOptions.values()).toList(),
            "Welcome to the app, choose which option you would like to select: "
    );

    return switch (choice) {
      case 0 -> {
        userController.login();
        yield true;
      }
      case 1 -> {
        userController.registerEntertainmentProvider();
        yield true;
      }
      default -> false;
    };
  }

  private boolean handleStudentMainMenu(){
    int choice = selectFromMenu(
            Arrays.stream(StudentMenuOptions.values()).toList(),
            "Welcome student, select from the below options: "
    );

    return switch (choice) {
      case 0 -> {
        userController.logout();
        yield true;
      }
      case 1 -> {
        eventPerformanceController.searchForPerformances();
        yield true;
      }
      case 2 -> {
        eventPerformanceController.viewPerformance();
        yield true;
      }
      case 3 -> {
        bookingController.reviewPerformance();
        yield true;
      }
      case 4 -> {
        userController.editPreferences();
        yield true;
      }
      case 5 -> {
        bookingController.bookPerformance();
        yield true;
      }
      case 6 -> {
        bookingController.cancelBooking();
        yield true;
      }
      default -> false;
    };
  }

  private boolean handleEntertainmentProviderMainMenu(){
    int choice = selectFromMenu(
            Arrays.stream(EPMenuOptions.values()).toList(),
            "Welcome admin, select from the below options: "
    );

    return switch (choice) {
      case 0 -> {
        userController.logout();
        yield true;
      }
      case 1 -> {
        eventPerformanceController.searchForPerformances();
        yield true;
      }
      case 2 -> {
        eventPerformanceController.viewPerformance();
        yield true;
      }
      case 3 -> {
        eventPerformanceController.createEvent();
        yield true;
      }
      case 4 -> {
        eventPerformanceController.cancelPerformance();
        yield true;
      }
      default -> false;
    };
  }

  private boolean handleAdminStaffMainMenu(){
    int choice = selectFromMenu(
            Arrays.stream(AdminMenuOptions.values()).toList(),
            "Welcome ep, select from the below options: "
    );

    return switch (choice) {
      case 0 -> {
        userController.logout();
        yield true;
      }
      case 1 -> {
        eventPerformanceController.searchForPerformances();
        yield true;
      }
      case 2 -> {
        eventPerformanceController.viewPerformance();
        yield true;
      }
      case 3 -> {
        eventPerformanceController.sponsorPerformance();
        yield true;
      }
      default -> false;
    };
  }
}
