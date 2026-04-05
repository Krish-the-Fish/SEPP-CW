package com.fortytwogroup.view;

import java.util.Collection;
import java.util.Scanner;

public class TextUserInterface implements View {
  private final Scanner scanner;

  private static final String ANSI_RED = "\\e[0;31m";
  private static final String ANSI_GREEN = "\\e[0;32m";
  private static final String ANSI_RESET = "\u001B[0m";

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
    System.out.println(ANSI_GREEN + successMessage + ANSI_RESET);
  }

  @Override
  public void displayError(String errorMessage) {
    System.out.println(ANSI_RED + errorMessage + ANSI_RESET);
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
