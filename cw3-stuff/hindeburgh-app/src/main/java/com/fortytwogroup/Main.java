package com.fortytwogroup;


import com.fortytwogroup.controller.BookingController;
import com.fortytwogroup.controller.EventPerformanceController;
import com.fortytwogroup.controller.MenuController;
import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.external.MockPaymentSystem;
import com.fortytwogroup.view.TextUserInterface;



public class Main { // Program starts here.
  public static void main(String[] args) {

    TextUserInterface UI = new TextUserInterface();
    MockPaymentSystem paymentSystem = new MockPaymentSystem();
    UserController userController = new UserController(UI, null);
    EventPerformanceController eventPerformanceController = new EventPerformanceController(UI, null, paymentSystem);
    BookingController bookingController = new BookingController(null, null, null);
    MenuController menuController = new MenuController(userController, eventPerformanceController, bookingController, UI);

    menuController.mainMenu();

  }
}
