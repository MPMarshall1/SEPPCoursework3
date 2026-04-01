package com.example;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class EventPerformanceController extends Controller {

    private PaymentSystem paymentSystem = new MockPaymentSystem();

    private long nextEventID;
    private long nextPerformanceID;

    private Collection<Event> allEvents;
    private Collection<Performance> allPerformances;

    public EventPerformanceController() {

        super();

        // initial ID numbers
        this.nextEventID = 1;
        this.nextPerformanceID = 1;

        this.allEvents = new ArrayList<>();
        this.allPerformances = new ArrayList<>();
    }


    public Event createEvent() {

        if (!checkCurrentUserIsEntertainmentProvider()) {
            System.out.println("Only Entertainment Providers can create events.");
            return null;
        }

        // scanner to ask about event details
        Scanner sc = new Scanner(System.in);
        System.out.println("Create new Event: ");

        // get event title
        System.out.print("Enter event title: ");
        String title = sc.nextLine();

        // get ticket status
        boolean isTicketed = false;

        // runs loop to validate input
        while (true) {

            System.out.println("Is the event ticketed? (yes/no): ");
            String ticketInput = sc.nextLine().trim().toLowerCase();

            if (ticketInput.equalsIgnoreCase("yes")) {
                isTicketed = true;
                break;
            }

            if (ticketInput.equalsIgnoreCase("no")) {
                isTicketed = false;
                break;
            }

            // if input invalid keep prompting
            else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'. ");
            }
        }

        // get event type
        EventType eventType = null;

        while (true) {

            System.out.println("Enter event type: (Music, Theatre, Dance, Movie, Sports): ");
            String type = sc.nextLine().trim();

            try {

                // try to convert input to EventType
                eventType = EventType.valueOf(type);
                break;  // if input is valid, break out of loop

            } catch (IllegalArgumentException e) {

                // if input invalid display message
                System.out.println("Invalid event type. Please try again.");
            }
        }

        // set user as organiser
        EntertainmentProvider organiser = (EntertainmentProvider) this.currentUser;

        // create event
        Event newEvent = new Event(organiser, this.nextEventID, title, eventType, isTicketed);
        this.addEvent(newEvent);
        this.nextEventID ++; // increment eventID for next events

        System.out.println("Successfully created event with ID: "+ newEvent.getEventID());
        return newEvent;
    }

    public void searchForPerformances() {

        // precondition: makes sure the user is logged in
        if (checkCurrentUserIsGuest()) {
            System.out.println("You must be logged in to search for performances");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Search Performances");

        // get required date from user
        LocalDate searchDate = promptForValidDate(sc);

        // find performances on required date
        List<Performance> foundPerformances = getPerformancesOnDate(searchDate);

        if (foundPerformances.isEmpty()) {
            System.out.println("There are no performances on the provided date");
            return;
        }

        // sort performances list based on preferences if the user is a student
        if (checkCurrentUserIsStudent()) {
            sortPerformancesForStudent(foundPerformances);
        }

        printPerformances(foundPerformances, searchDate);
    }

    // 4 helper methods for searchForPerformances

    // helper 1: handles date input validation
    private LocalDate promptForValidDate(Scanner sc) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {

            System.out.println("Enter date of performances (dd/MM/yyyy): ");
            String dateInput = sc.nextLine().trim();

            try {
                // try to parse input into Date object
                return LocalDate.parse(dateInput, formatter);

            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please try again.");
            }
        }
    }

    // helper 2: get performances on required date
    private List<Performance> getPerformancesOnDate(LocalDate searchDate) {

        List<Performance> foundPerformances = new ArrayList<>();

        for (Performance performance : allPerformances) {

            // get all performances on the provided date from allPerformances and add to foundPerformances
            if (performance.getStartDateTime().toLocalDate().equals(searchDate)) {
                foundPerformances.add(performance);
            }
        }
        return foundPerformances;
    }

    // helper 3: sort performances based on student preferences
    private void sortPerformancesForStudent(List<Performance> foundPerformances) {

        Student student = (Student) this.currentUser;
        StudentPreferences preferences = student.getPreferences();

        // check if student has at least one preference
        if (preferences != null && (preferences.preferMusicEvents || preferences.preferTheaterEvents ||
                preferences.preferDanceEvents || preferences.preferMovieEvents ||
                preferences.preferSportsEvents)) {

            // sort performances based on preferences
            foundPerformances.sort((perf1, perf2) -> {

                // checks if the type of this performance is one of the preferences
                boolean perf1Matches = preferences.matches(perf1.getEvent().getEventType());

                // do the same for the second performance to compare
                boolean perf2Matches = preferences.matches(perf2.getEvent().getEventType());

                // if perf1 type is a preference, move it up on the list
                if (perf1Matches && !perf2Matches) {
                    return -1;
                }

                // if perf2 type is a preference, move it up on the list
                if (!perf1Matches && perf2Matches) {
                    return 1;
                }

                // else, leave them where they are
                return 0;
            });
        }
    }

    // helper 4: prints list of performances
    private void printPerformances(List<Performance> performances, LocalDate searchDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("Performances on " + searchDate.format(formatter) + ":");
        for (Performance performance : performances) {

            System.out.println("ID: "+performance.getPerformanceId());
            System.out.println("Event Name: "+performance.getEvent().getEventTitle());
            System.out.println("Time: "+performance.getStartDateTime().toLocalTime() + " - "+performance.getEndDateTime().toLocalTime());
            System.out.println("Venue: "+performance.getVenueAddress());
            System.out.println("Entertainment Provider: "+performance.getEvent().getOrganiserName());

            // printing event review average
            System.out.println("Event average rating: "+performance.getEvent().getAverageRatingOfPerformances());

            System.out.println();
        }
    }


    public void viewPerformance() {

        // check user is logged in
        if (checkCurrentUserIsGuest()) {
            view.displayError("You must be logged in to view performance details");
            return;
        }

        // get the required performance from required ID
        Performance selectedPerformance = promptForValidPerformance();
        if (selectedPerformance == null) {

            // terminates use case if input = 'X'
            return;
        }

        // builds the string containing the performance details
        String details = buildPerformanceDetails(selectedPerformance);

        view.displaySpecificPerformance(details);
    }

    // helpers for viewPerformance

    // helper 1: handle input and get required performance
    private Performance promptForValidPerformance() {

        // validating performance ID
        while (true) {

            String input = view.getInput("Enter the performance ID to view or type 'X' to exit: ").trim();

            if (input.equalsIgnoreCase("X")) {
                return null;
            }

            try {
                long perfID = Long.parseLong(input);
                Performance selectedPerformance = getPerformanceByID(perfID);

                // if perfID is valid, exit loop
                if (selectedPerformance != null) {
                    return selectedPerformance;
                }

                else {
                    view.displayError("No performance found with that ID. Please try again.");
                }
            }   catch (NumberFormatException e) {
                view.displayError("Invalid format. Please try again.");
            }
        }
    }

    // helper 2: put together performance details to view
    private String buildPerformanceDetails(Performance selectedPerformance) {

        // putting together performance details
        Event event = selectedPerformance.getEvent();
        StringBuilder details = new StringBuilder();

        details.append("Performance ID: ").append(selectedPerformance.getPerformanceId()).append("\n");
        details.append("Event Title: ").append(event.getEventTitle()).append("\n");
        details.append("Event Type: ").append(event.getEventType()).append("\n");

        details.append("Organiser: ").append(event.getOrganiserName()).append("\n");

        details.append("Date and Time: ").append(selectedPerformance.getStartDateTime()).append(" to ")
                .append(selectedPerformance.getEndDateTime()).append("\n");

        details.append("Venue: ").append(selectedPerformance.getVenueAddress()).append("\n");

        // ticket availability
        if (selectedPerformance.checkIfEventIsTicketed()) {
            int ticketsRemaining = selectedPerformance.getNumTicketsTotal() - selectedPerformance.getNumTicketsSold();
            details.append("Ticket Status: ").append(ticketsRemaining).append(" tickets remaining\n");
            details.append("Ticket Price: £").append(selectedPerformance.getFinalTicketPrice()).append("\n");
        }

        else {
            details.append("Ticket Status: Free / Non-Ticketed Event\n");
        }

        // getting average review rating
        details.append("--- Reviews and Ratings ---\n");
        details.append("Event Average Rating: ").append(event.getAverageRatingOfPerformances()).append("/5\n");

        // list all reviews
        Collection<String> allReviews = event.getAllPerformanceReviews();

        if (allReviews == null || allReviews.isEmpty()) {
            details.append("There are no reviews for this event\n");
        }

        else {
            for (String review : allReviews) {
                details.append("- ").append(review).append("\n");
            }
        }

        return details.toString();
    }

    private boolean checkIfSponsorshipPossible(Performance performance, int amount) {

        if (!(performance.checkIfEventIsTicketed())) {
            return false;
        }

        double perfTicketCost = performance.getFinalTicketPrice();

        if (amount < 0 || amount > perfTicketCost) {
            return false;
        }

        return true;
    }

    public void cancelPerformance() {

        Performance performanceToCancel = promptForCancelPerformance();

        if (performanceToCancel == null) {
            return;
        }

        // get cancellation message
        String organiserMessage = promptForCancellationMessage();

        // process refunds
        if (performanceToCancel.hasActiveBookings()) {

            boolean refundsSuccessful = processCancellationRefunds(performanceToCancel, organiserMessage);

            if (!refundsSuccessful) {
                view.displayError("There was an error with a refund. Performance was not cancelled");
                return;
            }

            // if all refunds are successful, cancel the bookings
            for (Booking booking : performanceToCancel.getBookings()) {

                booking.cancelByProvider();
            }
        }
        performanceToCancel.cancel();
        view.displaySuccess("Cancellation Successful.");
    }

    // helpers for cancelPerformance

    // helper 1: handles input and checks if ID is valid
    private Performance promptForCancelPerformance() {

        while (true) {

            String input = view.getInput("Enter ID of performance to cancel or type 'X' to exit: ").trim();
            if (input.equalsIgnoreCase("X")) {
                return null;
            }

            try {
                long perfID = Long.parseLong(input);
                Performance performance = getPerformanceByID(perfID);

                // check if performance exists
                if (performance == null) {
                    view.displayError("No performance found with that ID. Please try again.");
                    continue;
                }

                // check if performance is owned by logged in EP
                if (!performance.checkCreatedByEP(currentUser.getEmail())) {
                    view.displayError("The performance with given ID does not belong to you.");
                    continue;
                }

                // check if performance already happened
                if (!performance.checkHasNotHappenedYet()) {
                    view.displayError("Performance can't be cancelled as it has already happened.");
                    continue;
                }

                return performance;
            }   catch (NumberFormatException e) {
                view.displayError("Invalid ID format. Please try again.");
            }
        }
    }

    // helper 2: checks for empty cancellation message
    private String promptForCancellationMessage() {

        while (true) {

            String message = view.getInput("Enter a cancellation message for students: ").trim();

            if (message.isEmpty()) {
                view.displayError("Message cannot be empty. Please enter a message.");
            }

            else {
                return message;
            }
        }
    }

    // helper 3: process the refunds
    private boolean processCancellationRefunds(Performance performance, String organiserMessage) {

        String eventTitle = performance.getEvent().getEventTitle();
        String EPemail = currentUser.getEmail();

        String bookingsDetails = performance.getBookingDetailsForRefund();

        // parse the booking details
        for (Booking booking : performance.getBookings()) {

            // only refund active bookings
            if (booking.getStatus() == BookingStatus.ACTIVE) {

                Student student = booking.getStudent();
                String studentEmail = student.getEmail();
                int studentPhone = student.getPhoneNumber();
                int numTickets = booking.getNumTickets();
                double amountPaid = booking.getAmountPaid();

                // process refund
                boolean refundSuccessful = paymentSystem.processRefund(
                        numTickets, eventTitle, studentEmail, studentPhone,
                        EPemail, amountPaid, organiserMessage);

                // if any refunds fail, terminate use case
                if (!refundSuccessful) {
                    return false;
                }
            }
        }
        return true;
    }


    public void addEvent(Event e) {
        allEvents.add(e);
    }

    public void addPerformance(Performance p) {
        allPerformances.add(p);
    }

    public Event getEventByID(long eventID) {

        for (Event event : allEvents) {

            if (event.getEventID() == eventID) {

                return event;
            }
        }
        return null;
    }

    public Event getEventByTitle(String title) {

        for (Event event : allEvents) {

            if (event.getEventTitle().equalsIgnoreCase(title)) {

                return event;
            }
        }
        return null;
    }

    public Performance getPerformanceByID(long performanceID) {

        for (Performance performance : allPerformances) {

            if (performance.getPerformanceId() == performanceID) {

                return performance;
            }
        }
        return null;
    }
}
// TODO: implement sponsor performance
