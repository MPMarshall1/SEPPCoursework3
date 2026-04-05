package com.example;

import java.util.ArrayList;
import java.util.Collection;

public class MenuController extends Controller {

    private UserController userController;
    private EventPerformanceController eventPerformanceController;
    private BookingController bookingController;

    Collection<Performance> allPerformance = new ArrayList<>();

    public MenuController() {
        super();
        userController = new UserController();
        eventPerformanceController = new EventPerformanceController(allPerformance, userController);
        bookingController = new BookingController(userController);
    }

    //Constructor allowing the injection of a custom view
    public MenuController(View view) {
        super(view);
        userController = new UserController(view);
        eventPerformanceController = new EventPerformanceController(allPerformance, userController, view);
        bookingController = new BookingController(userController, view);
    }

    public void mainMenu() {
        view.displaySuccess("System started. Enter 'X' to exit.");
        while (true) {
            Boolean guestMode = handleGuestMainMenu();
            while (guestMode!=null && guestMode) {
                guestMode = handleGuestMainMenu();
            }
            if (guestMode==null) {return;}

            Boolean studentMode = handleStudentMainMenu();
            while (studentMode!=null && studentMode) {
                studentMode = handleStudentMainMenu();
            }
            if (studentMode==null) {return;}

            Boolean EPMode = handleEntertainmentProviderMainMenu();
            while (EPMode!=null && EPMode) {
                EPMode = handleEntertainmentProviderMainMenu();
            }
            if (EPMode==null) {return;}

            Boolean adminMode = handleAdminMainMenu();
            while (adminMode!=null && adminMode) {
                adminMode = handleAdminMainMenu();
            }
            if (adminMode==null) {return;}
        }
    }

    Boolean handleGuestMainMenu() {
        if (!userController.checkCurrentUserIsGuest()) {
            return false;
        }

        String option = view.getInput("Enter option (LOGIN, REGISTER_EP): ").trim();

        try {
            if (option.equals("X")) {return null;}
            // try to convert input to EventType
            GuestMenuOptions action = GuestMenuOptions.valueOf(option);

            switch (action) {
                case GuestMenuOptions.LOGIN:
                    userController.login();
                    //Return as user changes
                    return false;
                case GuestMenuOptions.REGISTER_EP:
                    if (userController.registerEntertainmentProvider()==null) {return true;}
                    return false;
            }

        } catch (IllegalArgumentException e) {

            // if input invalid display message
            view.displayError("Invalid menu selection. Please try again.");
            return false;
        }
        return true;
    }

    Boolean handleStudentMainMenu() {
        if (!userController.checkCurrentUserIsStudent()) {
            return false;
        }

        String option = view.getInput("Enter option (LOGOUT, SERACH_FOR_PERFORMANCES, VIEW_PERFORMANCE, REVIEW_PERFORMANCE, EDIT_PREFERENCES, BOOK_EVENT, CANCEL_BOOKING): ").trim();

        try {
            if (option.equals("X")) {return null;}
            // try to convert input to EventType
            StudentMenuOptions action = StudentMenuOptions.valueOf(option);

            switch (action) {
                case StudentMenuOptions.LOGOUT:
                    userController.logout();
                    //Return as user changes
                    return false;
                case StudentMenuOptions.SEARCH_FOR_PERFORMANCES:
                    eventPerformanceController.searchForPerformances();
                    return true;
                case StudentMenuOptions.VIEW_PERFORMANCE:
                    eventPerformanceController.viewPerformance();
                    return true;
                case StudentMenuOptions.REVIEW_PERFORMANCE:
                    bookingController.reviewPerformance();
                    return true;
                case StudentMenuOptions.EDIT_PREFERENCES:
                    userController.editPreferences();
                    return true;
                case StudentMenuOptions.BOOK_EVENT:
                    allPerformance = eventPerformanceController.getAllPerformances();
                    bookingController.bookPerformance(allPerformance);
                    return true;
                case StudentMenuOptions.CANCEL_BOOKING:
                    bookingController.cancelBooking();
                    return true;
            }

        } catch (IllegalArgumentException e) {

            // if input invalid display message
            view.displayError("Invalid menu selection. Please try again.");
            return false;
        }
        return true;
    }

    Boolean handleEntertainmentProviderMainMenu() {
        if (!userController.checkCurrentUserIsEntertainmentProvider()) {
            return false;
        }

        String option = view.getInput("Enter option (LOGOUT, SEARCH_FOR_PERFORMANCES, VIEW_PERFORMANCE, CREATE_EVENT, CREATE_PERFORMANCE, CANCEL_PERFORMANCE): ").trim();

        try {
            if (option.equals("X")) {return null;}
            // try to convert input to EventType
            EPMenuOptions action = EPMenuOptions.valueOf(option);

            switch (action) {
                case EPMenuOptions.LOGOUT:
                    userController.logout();
                    //Return as user changes
                    return false;
                case EPMenuOptions.SEARCH_FOR_PERFORMANCES:
                    eventPerformanceController.searchForPerformances();
                    return true;
                case EPMenuOptions.VIEW_PERFORMANCE:
                    eventPerformanceController.viewPerformance();
                    return true;
                case EPMenuOptions.CREATE_EVENT:
                    eventPerformanceController.createEvent();
                    return true;
                case EPMenuOptions.CREATE_PERFORMANCE:
                    eventPerformanceController.addPerformance();
                    return true;
                case EPMenuOptions.CANCEL_PERFORMANCE:
                    eventPerformanceController.cancelPerformance();
                    return true;
            }

        } catch (IllegalArgumentException e) {

            // if input invalid display message
            view.displayError("Invalid menu selection. Please try again.");
            return false;
        }
        return true;
    }

    Boolean handleAdminMainMenu() {
        if (!userController.checkCurrentUserIsAdmin()) {
            return false;
        }

        String option = view.getInput("Enter option (LOGOUT, SEARCH_FOR_PERFORMANCES, VIEW_PERFORMANCE, SPONSOR_PERFORMANCE): ").trim();

        try {
            if (option.equals("X")) {return null;}
            // try to convert input to EventType
            AdminMenuOptions action = AdminMenuOptions.valueOf(option);

            switch (action) {
                case AdminMenuOptions.LOGOUT:
                    userController.logout();
                    //Return as user changes
                    return false;
                case AdminMenuOptions.SEARCH_FOR_PERFORMANCES:
                    eventPerformanceController.searchForPerformances();
                    return true;
                case AdminMenuOptions.VIEW_PERFORMANCE:
                    eventPerformanceController.viewPerformance();
                    return true;
                case AdminMenuOptions.SPONSOR_PERFORMANCE:
                    eventPerformanceController.sponsorPerformance();
                    return true;
            }

        } catch (IllegalArgumentException e) {

            // if input invalid display message
            view.displayError("Invalid menu selection. Please try again.");
            return false;
        }
        return true;
    }
}
