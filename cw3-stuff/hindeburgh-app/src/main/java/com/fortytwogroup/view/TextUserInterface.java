package com.fortytwogroup.view;

import java.util.Collection;
import java.util.Scanner;

public class TextUserInterface implements View {
  private final Scanner scanner;

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
    System.out.println(successMessage);
  }

  @Override
  public void displayError(String errorMessage) {
    System.out.println(errorMessage);
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

  }

}
