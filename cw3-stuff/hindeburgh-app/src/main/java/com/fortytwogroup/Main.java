package com.fortytwogroup;


import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.MenuController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.external.MockVerificationService;
import com.fortytwogroup.view.TextUserInterface;


/**
 * Contains the main method for a single user on the app.
 */
public class Main { // Program starts here.
  public static void main(String[] args) {

    TextUserInterface textUserInterface = new TextUserInterface();

    MockPaymentSystem paymentSystem = new MockPaymentSystem();

    MockVerificationService verificationService = new MockVerificationService();

    UserController userController = new UserController(
        textUserInterface,
        verificationService);

    EventPerformanceController eventPerformanceController = new EventPerformanceController(
        textUserInterface,
        paymentSystem);

    BookingController bookingController = new BookingController(
        textUserInterface,
        paymentSystem);

    MenuController menuController = new MenuController(
        userController,
        eventPerformanceController,
        bookingController,
        textUserInterface);

    // now share the initialize performances collection among relevant controllers
    eventPerformanceController.setPerformances(menuController.getPerformances());
    bookingController.setPerformances(menuController.getPerformances());

    // dependencies now all injected
    // can now give the user the main menu

    menuController.mainMenu();

  }
}
