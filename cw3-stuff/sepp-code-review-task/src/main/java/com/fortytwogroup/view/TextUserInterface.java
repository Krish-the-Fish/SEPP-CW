package com.fortytwogroup.view;

import java.util.Scanner;

/**
 * Class for interfacing with the end user to accept user input, return error/success messages
 * to the end user, and prompt the end user for input
 */
public class TextUserInterface {

  /**
   * Prompts user to input email address in the cli and accepts input as type String
   * @return String containing raw input from cli
   */
  public String getEmailInput() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter your email address: ");
    return scanner.nextLine();
  }

  /**
   * Prompts user to enter their account password in the cli and accepts input type String
   * @return String containing raw input from the cli
   */
  public String getPasswordInput() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter your password: ");
    return scanner.nextLine();
  }

  /**
   * Displays a login failure message
   */
  public void displayLoginErrorMessage() {
    System.out.println("Login Failed");
  }

}
