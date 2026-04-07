package com.fortytwogroup.view;

import java.util.Collection;

/**
 * Interface defining methods our I/O class should have in order to successfully
 * interact with users and the system.
 */
public interface View {
  public String getInput(String inputPrompt);
  public void displaySuccess(String successMessage);
  public void displayError(String errorMessage);
  public void listOfPerformances(Collection<String> listOfPerformanceInfo);
  public void displaySpecificPerformance(String performanceInfo);
  public void displayBookingRecord(String bookingRecord);

}
