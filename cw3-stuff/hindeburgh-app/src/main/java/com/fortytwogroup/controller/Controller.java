package com.fortytwogroup.controller;

import java.util.Collection;

public abstract class Controller {
  protected boolean checkCurrentUserIsGuest() {
    return false;
  }

  protected boolean checkCurrentUserIsAdmin() {
    return false;
  }

  protected boolean checkCurrentUserIsStudent() {
    return false;
  }

  protected boolean checkCurrentUserIsEntertainmentProvider() {
    return false;
  }

  /* no name provided in spec for String parameter,
    therefore using item as suitable placeholder
   */
  protected <T> int selectFromMenu(Collection<T> collection, String item) {
    return 0;
  }
}
