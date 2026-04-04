package com.example;

public class StudentPreferences {

    public boolean preferMusicEvents;
    public boolean preferTheaterEvents;
    public boolean preferDanceEvents;
    public boolean preferMovieEvents;
    public boolean preferSportsEvents;

    public StudentPreferences() {
        this.preferMusicEvents = false;
        this.preferTheaterEvents = false;
        this.preferDanceEvents = false;
        this.preferMovieEvents = false;
        this.preferSportsEvents = false;
    }

    public boolean updatePreferences(String studentRawStringPreferences) {

        // if input is empty, preferences are reset to default
        if (studentRawStringPreferences == null || studentRawStringPreferences.trim().isEmpty()) {

            this.preferMusicEvents = false;
            this.preferTheaterEvents = false;
            this.preferDanceEvents = false;
            this.preferMovieEvents = false;
            this.preferSportsEvents = false;
            return true;
        }

        // split the input by commas or spaces and store the words
        String[] words = studentRawStringPreferences.trim().toLowerCase().split("[,\\s]+");

        // limits the number of preferences to 3
        if (words.length > 3) {
            return false;
        }

        // use temporary preferences variables so that main variables are overwritten after validating input
        boolean tempMusic = false;
        boolean tempTheater = false;
        boolean tempDance = false;
        boolean tempMovie = false;
        boolean tempSports = false;

        // loop through input words to update preferences
        for (String word : words) {

            switch (word) {
                case "music":
                    tempMusic = true;
                    break;
                case "theater":
                case "theatre":
                    tempTheater = true;
                    break;
                case "dance":
                    tempDance = true;
                    break;
                case "movie":
                    tempMovie = true;
                    break;
                case "sports":
                case "sport":
                    tempSports = true;
                    break;
                default:
                    return false;
            }
        }
        // save preferences
        this.preferMusicEvents = tempMusic;
        this.preferTheaterEvents = tempTheater;
        this.preferDanceEvents = tempDance;
        this.preferMovieEvents = tempMovie;
        this.preferSportsEvents = tempSports;

        return true;
    }


    // helper method
    // takes in specific event type and returns whether its one of the preferences
    public boolean matches (EventType type) {

        if (type == null) {
            return false;
        }

        switch (type) {
            case Music:
                return this.preferMusicEvents;
            case Theatre:
                return this.preferTheaterEvents;
            case Dance:
                return this.preferDanceEvents;
            case Movie:
                return this.preferMovieEvents;
            case Sports:
                return this.preferSportsEvents;
            default:
                return false;
        }
    }
}
