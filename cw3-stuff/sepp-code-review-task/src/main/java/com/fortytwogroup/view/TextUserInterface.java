package com.fortytwogroup.view;

import java.util.Scanner;

/**
 * Class for interfacing with the end user to accept user input, return error/success messages
 * to the end user, and prompt the end user for input
 */
public class TextUserInterface {

  /**
   * Prompts user using the message argument to enter  the cli and accepts input as type String
   * @param prompt String containing a message for the user to prompt them to enter info
   * @return String containing raw input from cli
   */
  public String getUserInput(String prompt) {
    Scanner scanner = new Scanner(System.in);
    System.out.print(prompt);
    return scanner.nextLine();
  }


  /**
   * Displays a login failure message
   * @param errorMessage error message to be returned when method invoked
   */
  public void displayErrorMessage(String errorMessage) {
    System.out.println(errorMessage);
  }

  /**
   * Displays a general failure message
   * @param message message to be returned when method invoked
   */
  public void displayGeneralMessage(String message) {
    System.out.println(message);
  }

}
