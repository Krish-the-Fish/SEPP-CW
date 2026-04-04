package com.fortytwogroup.controller;

import com.fortytwogroup.model.AdminStaff;
import com.fortytwogroup.model.EntertainmentProvider;
import com.fortytwogroup.model.Student;
import com.fortytwogroup.model.User;

import java.util.Collection;

public abstract class Controller {

  public Controller() {

  }
  private User currentUser = null;

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  protected boolean checkCurrentUserIsGuest() {
    return !checkCurrentUserIsAdmin()
            && !checkCurrentUserIsStudent()
            && !checkCurrentUserIsEntertainmentProvider();
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
  protected <T> int selectFromMenu(Collection<T> collection, String item) {
    return 0;
  }
}
