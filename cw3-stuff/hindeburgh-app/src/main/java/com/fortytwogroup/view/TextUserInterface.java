package com.fortytwogroup.view;

import java.util.Collection;
import java.util.Scanner;

public class TextUserInterface implements View {
  private final Scanner scanner;

  private final String ANSI_RED = "\u001B[0;31m";
  private final String ANSI_GREEN = "\u001B[0;32m";
  private final String ANSI_RESET = "\u001B[0m";

  public TextUserInterface() {
    this.scanner = new Scanner(System.in);
  }

  @Override
  public String getInput(String inputPrompt) {
    System.out.print(inputPrompt);
    return scanner.nextLine();
  }

  @Override
  public void displaySuccess(String successMessage) {
    System.out.print(ANSI_GREEN);
    System.out.println(successMessage);
    System.out.print(ANSI_RESET);
  }

  @Override
  public void displayError(String errorMessage) {
    System.out.print(ANSI_RED);
    System.out.println(errorMessage);
    System.out.print(ANSI_RESET);
  }

  @Override
  public void listOfPerformances(Collection<String> listOfPerformanceInfo) {
    for (String performance : listOfPerformanceInfo) {
      displaySpecificPerformance(performance);
    }
  }

  @Override
  public void displaySpecificPerformance(String performanceInfo) {

  }

  @Override
  public void displayBookingRecord(String bookingRecord) {
    System.out.println("-----Booking Record-----");
    System.out.println(bookingRecord);
  }

}
