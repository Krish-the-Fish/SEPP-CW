package com.fortytwogroup.controller;

import com.fortytwogroup.model.AdminStaff;
import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.model.User;
import com.fortytwogroup.view.TextUserInterface;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for defining common methods among the controller classes in the system.
 * Also contains a reference to the active user on the thread.
 * Class is not meant to be instantiated and is hence abstract.
 */
public abstract class Controller {
  private TextUserInterface textUserInterface;

  /**
   * Allows dependency injection, simplifying code in main method
   * @param textUserInterface reference to the ui object that abstracts user I/O
   */
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

  /*
  no name provided in spec for String parameter,
   therefore using item as suitable placeholder for String message in selectFromMenu method
   */

  /**
   * Prompts the user to select form a list of options, allowing for initiation of use-cases
   * @param collection collection of all options on the menu screen given to the user
   * @param message prompt given to user to tell them to select an option from the menu
   * @return the number representing the option the user chose from the menu. In case of error,
   * return -1 as sentinel value
   * @param <T> generic allowing for menu options to be of any type, provided the types of the
   *          options are consistent with each other.
   */
  protected <T> int selectFromMenu(Collection<T> collection, String message) {
    ArrayList<String> options = optionStrings(collection);
    System.out.println(message);
    System.out.println(String.join(", ", options));
    String input = textUserInterface.getInput("Command: ");
    int counter = 0;
    for (T item : collection) {
      if (input.equalsIgnoreCase(item.toString())) {
        return counter;
      }
      counter++;
    }
    return -1;
  }

  /**
   * Converts a collection of items into a list of their string representations.
   * * @param collection The collection of items to convert.
   * @return An ArrayList containing the string representation of each item in the collection.
   * @param <T> The type of the elements in the collection.
   */
  private <T> ArrayList<String> optionStrings(Collection<T> collection) {
    ArrayList<String> options = new ArrayList<>();
    for (T item : collection) {
      options.add(item.toString());
    }
    return options;
  }
}
