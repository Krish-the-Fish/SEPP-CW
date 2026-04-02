package com.fortytwogroup.controller;

import java.util.Arrays;
import java.util.Collection;

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

  // Unsure if this is the best way or location to instantiate
  // temporary to resolve errors
  UserController userController = new UserController();
  EventPerformanceController eventPerformanceController = new EventPerformanceController();
  BookingController bookingController = new BookingController();


  public void mainMenu() {
    // How are we meant to get input here?
    
    if (checkCurrentUserIsGuest()) {
      // Something supposed to happen with the boolean output?
      boolean out = handleGuestMainMenu();
    }
    else if (checkCurrentUserIsStudent()) {
      boolean out = handleStudentMainMenu();
    }
    else if (checkCurrentUserIsEntertainmentProvider()) {
      boolean out = handleEntertainmentProviderMainMenu();
    }
    else if (checkCurrentUserIsAdmin()) {
      boolean out = handleAdminStaffMainMenu();
    }
  }

  private boolean handleGuestMainMenu(){
    //Placeholder Input
    String input = "";

    int choice = selectFromMenu(Arrays.stream(GuestMenuOptions.values()).toList(), input);

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
    //Placeholder Input
    String input = "";

    int choice = selectFromMenu(Arrays.stream(StudentMenuOptions.values()).toList(), input);

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
    //Placeholder Input
    String input = "";

    int choice = selectFromMenu(Arrays.stream(EPMenuOptions.values()).toList(), input);

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
    //Placeholder Input
    String input = "";

    int choice = selectFromMenu(Arrays.stream(AdminMenuOptions.values()).toList(), input);

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
