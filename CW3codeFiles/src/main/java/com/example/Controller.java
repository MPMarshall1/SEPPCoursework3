package com.example;

import java.util.Collection;

public abstract class Controller {

    protected User currentUser;
    protected View view = new TextUserInterface();

    public Controller() {
        this.currentUser = null;
    }

    protected boolean checkCurrentUserIsGuest() {

        return this.currentUser == null;
    }

    protected boolean checkCurrentUserIsEntertainmentProvider() {

        if (this.currentUser == null) {
            return false;
        }

        return this.currentUser instanceof EntertainmentProvider;
    }
    protected boolean checkCurrentUserIsStudent() {

        if (this.currentUser == null) {
            return false;
        }

        return this.currentUser instanceof Student;
    }

    protected boolean checkCurrentUserIsAdmin() {

        if (this.currentUser == null) {
            return false;
        }

        return this.currentUser instanceof AdminStaff;
    }

    protected <T> int selectFromMenu(Collection<T> options, String prompt) {

        while (true) {

            // menu title
            view.displaySuccess("\n" + prompt);

            // loop through options and print as a list
            int i = 1;
            for (T option : options) {

                view.displaySuccess(i + ". " + option.toString());
                i++;
            }

            String input = view.getInput("Enter your choice: ").trim();

            try {

                // check if input is valid
                int choice = Integer.parseInt(input);

                if (choice >= 1 && choice <= options.size()) {
                    return choice;
                }

                else {
                    view.displayError("Invalid choice. Please try again. ");
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid input. Please enter a valid number.");
            }
        }
    }
}
