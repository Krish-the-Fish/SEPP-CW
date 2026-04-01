package com.fortytwogroup.controller;

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


  public void mainMenu() {

  }

  private boolean handleGuestMainMenu(){
    return false;
  }

  private boolean handleStudentMainMenu(){
    return false;
  }

  private boolean handleEntertainmentProviderMainMenu(){
    return false;
  }

  private boolean handleAdminStaffMainMenu(){
    return false;
  }
}
