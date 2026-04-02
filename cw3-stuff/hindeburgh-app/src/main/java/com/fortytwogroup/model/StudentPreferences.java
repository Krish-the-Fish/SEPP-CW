package com.fortytwogroup.model;

import java.util.Arrays;
import java.util.List;

public class StudentPreferences {
  public boolean preferMusicEvents;
  public boolean preferTheatreEvents;
  public boolean preferDanceEvents;
  public boolean preferMovieEvents;
  public boolean preferSportsEvents;

  public boolean updatePreferences(String studentRawStringPreferences) {
    // assuming that preferences string is in the form "Music Dance Sports"
    // guessing how this is how it is meant to work, set all to false then add them back as true

    preferMusicEvents = false;
    preferTheatreEvents = false;
    preferDanceEvents = false;
    preferMovieEvents = false;
    preferSportsEvents = false;

    String[] preferences = studentRawStringPreferences.split(" ");
    for (String preference : preferences) {
      switch (preference) {
        case "Music":
          preferMusicEvents = true;
          break;
        case "Theatre":
          preferTheatreEvents = true;
          break;
        case "Dance":
          preferDanceEvents = true;
          break;
        case "Movie":
          preferMovieEvents = true;
          break;
        case "Sports":
          preferSportsEvents = true;
          break;
      }
    }
    return true;
  }
}
