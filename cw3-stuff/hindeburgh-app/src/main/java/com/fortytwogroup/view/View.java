package com.fortytwogroup.view;

import java.util.Collection;

public interface View {
  public String getInput(String inputPrompt);
  public void displaySuccess(String successMessage);
  public void displayError(String errorMessage);
  public void listOfPerformances(Collection<String> listOfPerformanceInfo);
  public void displaySpecificPerformance(String performanceInfo);
  public void displayBookingRecord(String bookingRecord);

}
