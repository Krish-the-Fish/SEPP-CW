package com.fortytwogroup.controller;

import com.fortytwogroup.model.AdminStaff;
import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.model.User;
import com.fortytwogroup.view.TextUserInterface;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Controller {
  private TextUserInterface textUserInterface;

  protected void setTextUserInterface(TextUserInterface textUserInterface) {
    this.textUserInterface = textUserInterface;
  }

  private static User currentUser = null;

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  protected boolean checkCurrentUserIsGuest() {
    return currentUser == null;
  }

  protected boolean checkCurrentUserIsAdmin() {
    return currentUser instanceof AdminStaff;
  }

  protected boolean checkCurrentUserIsStudent() {
    return currentUser instanceof Student;
  }

  protected boolean checkCurrentUserIsEntertainmentProvider() {
    return currentUser instanceof EntertainmentProvider;
  }

  /* no name provided in spec for String parameter,
    therefore using item as suitable placeholder
   */
  protected <T> int selectFromMenu(Collection<T> collection, String message) {
    ArrayList<String> options = optionStrings(collection);
    System.out.println(message);
    System.out.println(String.join(", ", options));
    String input = textUserInterface.getInput("Command: ");
    int counter = 0;
    for (T item : collection) {
      if (input.toUpperCase().equals(item.toString())) {
        return counter;
      }
      counter++;
    }
    return -1;
  }

  private <T> ArrayList<String> optionStrings(Collection<T> collection) {
    ArrayList<String> options = new ArrayList<>();
    for (T item : collection) {
      options.add(item.toString());
    }
    return options;
  }
}
