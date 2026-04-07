package com.fortytwogroup.model;


/**
 * Class that contains the defined preferences for a given student, i.e. the student defines the
 * preferences themselves.
 */
public class StudentPreferences {
  public boolean preferMusicEvents;
  public boolean preferTheatreEvents;
  public boolean preferDanceEvents;
  public boolean preferMovieEvents;
  public boolean preferSportsEvents;

  /**
   * Alters the state of a given user's preferences object given changes they may make to it.
   * @param studentRawStringPreferences String literal containing the types of events that the
   *                                    student is interested in.
   * @return true if they are interested in a certain type of event, otherwise return false
   */
  public boolean updatePreferences(String studentRawStringPreferences) {
    // assuming that preferences string is in the form "Music Dance Sports"
    // guessing how this is how it is meant to work, set all to false then add them back as true

    preferMusicEvents = false;
    preferTheatreEvents = false;
    preferDanceEvents = false;
    preferMovieEvents = false;
    preferSportsEvents = false;

    String[] preferences = studentRawStringPreferences.split("\\s*,\\s*");
    for (String preference : preferences) {
      switch (preference.toUpperCase()) {
        case "MUSIC":
          preferMusicEvents = true;
          break;
        case "THEATRE":
          preferTheatreEvents = true;
          break;
        case "DANCE":
          preferDanceEvents = true;
          break;
        case "MOVIE":
          preferMovieEvents = true;
          break;
        case "SPORTS":
          preferSportsEvents = true;
          break;
      }
    }
    return true;
  }
}
