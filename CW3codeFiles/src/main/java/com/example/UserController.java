package com.example;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Controller responsible for user account management use cases:
 * log in, log out, register entertainment provider, and edit preferences.
 *
 * <p>Students and admin staff are pre-registered; entertainment providers
 * self-register at runtime via {@link #registerEntertainmentProvider()}.
 */
public class UserController extends Controller {

    private Collection<User> allUsers;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public UserController() {
        super();
        this.allUsers = new ArrayList<>();
        initialisePreRegisteredUsers();
    }

    // constructor allowing injection of a custom View (e.g. a mock for testing)
    public UserController(View view) {
        super(view);
        this.allUsers = new ArrayList<>();
        initialisePreRegisteredUsers();
    }

    // -------------------------------------------------------------------------
    // Pre-registered users (students and admin)
    // -------------------------------------------------------------------------

    /**
     * Populates the user store with hard-coded pre-registered students and
     * admin staff, as required by the spec.
     */
    private void initialisePreRegisteredUsers() {
        allUsers.add(new Student("student1@ed.ac.uk", "student1pass", "Alice Johnson", 111222333));
        allUsers.add(new Student("student2@ed.ac.uk", "student2pass", "Bob Williams", 444555666));
        allUsers.add(new AdminStaff("admin@ed.ac.uk", "adminpass", "Admin User"));
    }

    // -------------------------------------------------------------------------
    // Use case: Log In
    // -------------------------------------------------------------------------

    /**
     * Allows any registered user (student, entertainment provider, or admin)
     * to log in with their email and password.
     *
     * @return the authenticated {@link User}, or {@code null} if login fails
     */
    public User login() {

        // precondition: must not already be logged in
        if (!checkCurrentUserIsGuest()) {
            view.displayError("You are already logged in. Please log out first.");
            return null;
        }

        String email = view.getInput("Enter email: ").trim();
        String password = view.getInput("Enter password: ").trim();

        // search all registered users for matching credentials
        for (User user : allUsers) {

            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                this.currentUser = user;
                view.displaySuccess("Successfully logged in as " + email + ".");
                return user;
            }
        }

        view.displayError("Incorrect email or password. Please try again.");
        return null;
    }

    // -------------------------------------------------------------------------
    // Use case: Log Out
    // -------------------------------------------------------------------------

    /**
     * Logs out the currently authenticated user.
     */
    public void logout() {

        // precondition: must be logged in
        if (checkCurrentUserIsGuest()) {
            view.displayError("You are not currently logged in.");
            return;
        }

        String email = this.currentUser.getEmail();
        this.currentUser = null;
        view.displaySuccess("Successfully logged out (" + email + ").");
    }

    // -------------------------------------------------------------------------
    // Use case: Register Entertainment Provider
    // -------------------------------------------------------------------------

    /**
     * Allows a guest to register a new entertainment provider account.
     * After successful registration the new provider is added to the user
     * store so they can log in immediately.
     *
     * @return the newly created {@link EntertainmentProvider}, or {@code null}
     *         if registration fails
     */
    public EntertainmentProvider registerEntertainmentProvider() {

        // precondition: must be a guest (not already logged in)
        if (!checkCurrentUserIsGuest()) {
            view.displayError("You must be a guest to register a new account.");
            return null;
        }

        String orgName       = view.getInput("Enter organisation name: ").trim();
        String businessNumber = view.getInput("Enter business registration number: ").trim();
        String repName       = view.getInput("Enter representative name: ").trim();
        String description   = view.getInput("Enter organisation description: ").trim();
        String email         = view.getInput("Enter email address: ").trim();

        // extension 1a: check email is not already in use
        for (User existing : allUsers) {

            if (existing.getEmail().equalsIgnoreCase(email)) {
                view.displayError("That email address is already registered.");
                return null;
            }
        }

        // validate that no field is blank
        if (orgName.isEmpty() || businessNumber.isEmpty() || repName.isEmpty()
                || description.isEmpty() || email.isEmpty()) {
            view.displayError("All fields are required. Registration failed.");
            return null;
        }

        String password = view.getInput("Enter password: ").trim();

        if (password.isEmpty()) {
            view.displayError("Password cannot be empty. Registration failed.");
            return null;
        }

        EntertainmentProvider newEP = new EntertainmentProvider(
                email, password, orgName, businessNumber, repName, description);

        allUsers.add(newEP);

        view.displaySuccess("Entertainment provider registered successfully: " + orgName);
        return newEP;
    }

    // -------------------------------------------------------------------------
    // Use case: Edit Preferences
    // -------------------------------------------------------------------------

    /**
     * Allows the currently logged-in student to update their event type
     * preferences.  Up to three comma-separated event type names are accepted
     * (Music, Theatre, Dance, Movie, Sports).  An empty input resets all
     * preferences to false.
     */
    public void editPreferences() {

        // precondition: must be logged in as a student
        if (!checkCurrentUserIsStudent()) {
            view.displayError("Only students can edit event preferences.");
            return;
        }

        Student student = (Student) this.currentUser;
        StudentPreferences prefs = student.getPreferences();

        // display current preferences
        view.displaySuccess("Current preferences — "
                + "Music: "   + prefs.preferMusicEvents
                + ", Theatre: " + prefs.preferTheaterEvents
                + ", Dance: "   + prefs.preferDanceEvents
                + ", Movie: "   + prefs.preferMovieEvents
                + ", Sports: "  + prefs.preferSportsEvents);

        String input = view.getInput(
                "Enter preferred event types (Music, Theatre, Dance, Movie, Sports), "
                + "up to 3 separated by commas, or leave blank to clear: ").trim();

        boolean success = prefs.updatePreferences(input);

        if (success) {
            view.displaySuccess("Preferences updated successfully.");
        } else {
            view.displayError("Invalid preferences input. "
                    + "Please enter up to 3 types from: Music, Theatre, Dance, Movie, Sports.");
        }
    }

    // -------------------------------------------------------------------------
    // Accessors for cross-controller use
    // -------------------------------------------------------------------------

    /**
     * Returns all registered users (read-only view).
     * Exposed so that system tests or other controllers can inspect the store.
     *
     * @return unmodifiable collection of all users
     */
    public Collection<User> getAllUsers() {
        return allUsers;
    }
}
