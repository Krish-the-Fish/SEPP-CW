package com.fortytwogroup.model;



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
