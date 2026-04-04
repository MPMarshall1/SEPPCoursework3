package com.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class EventPerformanceController extends Controller {

    private PaymentSystem paymentSystem = new MockPaymentSystem();

    private long nextEventID;
    private long nextPerformanceID;
    private long nextBookingID;

    private Collection<Event> allEvents;
    private Collection<Performance> allPerformances;

    public EventPerformanceController() {

        super();

        // initial ID numbers
        this.nextEventID = 1;
        this.nextPerformanceID = 1;
        this.nextBookingID = 1;

        this.allEvents = new ArrayList<>();
        this.allPerformances = new ArrayList<>();
    }

    // constructor allowing injection of a custom View
    public EventPerformanceController(View view) {

        super(view);

        this.nextEventID = 1;
        this.nextPerformanceID = 1;
        this.nextBookingID = 1;

        this.allEvents = new ArrayList<>();
        this.allPerformances = new ArrayList<>();
    }


    public Event createEvent() {

        if (!checkCurrentUserIsEntertainmentProvider()) {
            view.displayError("Only Entertainment Providers can create events.");
            return null;
        }

        String title = view.getInput("Enter event title: ");

        // get ticket status
        boolean isTicketed = false;

        // runs loop to validate input
        while (true) {

            String ticketInput = view.getInput("Is the event ticketed? (yes/no): ").trim().toLowerCase();

            if (ticketInput.equalsIgnoreCase("yes")) {
                isTicketed = true;
                break;
            }

            else if (ticketInput.equalsIgnoreCase("no")) {
                isTicketed = false;
                break;
            }

            // if input invalid keep prompting
            else {
                view.displayError("Invalid input. Please enter 'yes' or 'no'. ");
            }
        }

        // get event type
        EventType eventType = null;

        while (true) {

            String type = view.getInput("Enter event type: (Music, Theatre, Dance, Movie, Sports): ").trim();

            try {
                // try to convert input to EventType
                eventType = EventType.valueOf(type);
                break;  // if input is valid, break out of loop

            } catch (IllegalArgumentException e) {

                // if input invalid display message
                view.displayError("Invalid event type. Please try again.");
            }
        }

        // set user as organiser
        EntertainmentProvider organiser = (EntertainmentProvider) this.currentUser;

        // create event
        Event newEvent = new Event(organiser, this.nextEventID, title, eventType, isTicketed);

        this.addEvent(newEvent);
        organiser.addEvent(newEvent);

        this.nextEventID ++; // increment eventID for next events

        view.displaySuccess("Successfully created event with ID: "+ newEvent.getEventID());
        return newEvent;
    }

    public void searchForPerformances() {

        // precondition: makes sure the user is logged in
        if (checkCurrentUserIsGuest()) {
            view.displayError("You must be logged in to search for performances");
            return;
        }

        // get required date from user
        LocalDate searchDate = promptForValidDate();

        // find performances on required date
        List<Performance> foundPerformances = getPerformancesOnDate(searchDate);

        if (foundPerformances.isEmpty()) {
            view.displayError("There are no performances on the provided date");
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
    private LocalDate promptForValidDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {

            String dateInput = view.getInput("Enter date of performances (dd/MM/yyyy): ").trim();

            try {
                // try to parse input into Date object
                return LocalDate.parse(dateInput, formatter);

            } catch (DateTimeParseException e) {
                view.displayError("Invalid format. Please try again.");
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

        List<String> formattedPerformances = new ArrayList<>();

        formattedPerformances.add("Performances on " + searchDate.format(formatter) + ":");

        for (Performance performance : performances) {

            StringBuilder builder = new StringBuilder();

            builder.append("ID: ").append(performance.getPerformanceId()).append("\n");
            builder.append("Event Name: ").append(performance.getEvent().getEventTitle()).append("\n");
            builder.append("Time: ").append(performance.getStartDateTime().toLocalTime())
                    .append(" - ").append(performance.getEndDateTime().toLocalTime()).append("\n");
            builder.append("Venue").append(performance.getVenueAddress()).append("\n");
            builder.append("Entertainment Provider: ").append(performance.getEvent().getOrganiserName()).append("\n");

            // printing event review average
            builder.append("Event average rating: ").append(performance.getEvent().getAverageRatingOfPerformances()).append("\n");

            formattedPerformances.add(builder.toString());
        }

        view.displayListofPerformances(formattedPerformances);
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


    public void cancelPerformance() {

        // precondition: must be an entertainment provider
        if (!(this.currentUser instanceof EntertainmentProvider)) {
            view.displayError("Only entertainment providers can cancel performances.");
            return;
        }

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


    public void sponsorPerformance() {

        // precondition: must be logged in as an entertainment provider
        if (!(this.currentUser instanceof EntertainmentProvider)) {
            view.displayError("Only entertainment providers can sponsor performances.");
            return;
        }

        Performance performanceToSponsor = promptForValidTicketedPerformance();

        if (performanceToSponsor == null) {
            // terminate use case
            return;
        }

        double amount = promptForSponsorshipAmount(performanceToSponsor);

        if (amount < 0) {
            return;
        }

        performanceToSponsor.sponsor(amount);
        view.displaySuccess("Sponsorship Successful!");
    }

    // helpers for sponsorPerformance

    // helper 1: get performance ID
    private Performance promptForValidTicketedPerformance() {

        while (true) {

            String IDinput = view.getInput("Enter performance ID to sponsor or type 'X' to exit: ").trim();

            if (IDinput.equalsIgnoreCase("X")) {
                return null;
            }

            try {

                long perfID = Long.parseLong(IDinput);
                Performance performance = getPerformanceByID(perfID);

                // extension 1a: check if ID is correct
                if (performance == null) {
                    view.displayError("Performance with given number does not exist");
                    return null;
                }

                // extension 1b: check if performance is ticketed
                if (!performance.checkIfEventIsTicketed()) {

                    view.displayError("The requested performance's event is non ticketed. It cannot be sponsored");
                    return null;
                }

                return performance;

            }   catch (NumberFormatException e) {
                    view.displayError("Invalid ID format. Please try again.");
            }
        }
    }

    // helper 2: get sponsorship amount
    private double promptForSponsorshipAmount(Performance performance) {

        while (true) {

            String amountInput = view.getInput("Enter the sponsorship amount or type 'X' to exit: ").trim();

            if (amountInput.equalsIgnoreCase("X")) {
                return -1.0;
            }

            try {

                double amount = Double.parseDouble(amountInput);

                // extension 3a
                if (!checkIfSponsorshipPossible(performance, amount)) {
                    continue;
                }

                return amount;
            }   catch (NumberFormatException e) {
                    view.displayError("The amount provided is invalid");
            }
        }
    }

    // changes 'amount' data type to double instead of int to match 'ticketPrice' variable in 'Performance' class
    // helper 3: checks for valid sponsorship amount
    private boolean checkIfSponsorshipPossible(Performance performance, double amount) {

        double perfTicketCost = performance.getFinalTicketPrice();

        if (amount < 0 || amount > perfTicketCost) {

            view.displayError("The amount provided is invalid.");
            return false;

        }
        return true;
    }


    public Performance addPerformance() {

        // precondition: must be an entertainment provider
        if (!checkCurrentUserIsEntertainmentProvider()) {
            view.displayError("Only Entertainment Providers can add performances.");
            return null;
        }

        // get and validate the event ID
        Performance newPerformance = null;

        while (true) {

            String eventIDInput = view.getInput(
                    "Enter event ID to add performance to or type 'X' to exit: ").trim();

            if (eventIDInput.equalsIgnoreCase("X")) {
                return null;
            }

            try {
                long eventID = Long.parseLong(eventIDInput);
                Event event = getEventByID(eventID);

                if (event == null) {
                    view.displayError("No event found with that ID. Please try again.");
                    continue;
                }

                // check the event belongs to the current EP
                if (!event.getOrganiserEmail().equals(currentUser.getEmail())) {
                    view.displayError("That event does not belong to you.");
                    continue;
                }

                // collect performance details
                LocalDateTime startDateTime = promptForValidDateTime(
                        "Enter performance start date/time (dd/MM/yyyy HH:mm): ");
                LocalDateTime endDateTime   = promptForValidDateTime(
                        "Enter performance end date/time   (dd/MM/yyyy HH:mm): ");

                if (endDateTime.isBefore(startDateTime) || endDateTime.isEqual(startDateTime)) {
                    view.displayError("End date/time must be after start date/time.");
                    continue;
                }

                String performersInput = view.getInput(
                        "Enter performer names (comma-separated): ").trim();
                List<String> performerNames = new ArrayList<>();

                for (String name : performersInput.split(",")) {
                    String trimmed = name.trim();
                    if (!trimmed.isEmpty()) {
                        performerNames.add(trimmed);
                    }
                }

                String venueAddress = view.getInput("Enter venue address: ").trim();

                int venueCapacity = promptForPositiveInt("Enter venue capacity: ");

                boolean venueOutdoors      = promptForYesNo("Is the venue outdoors? (yes/no): ");
                boolean venueAllowsSmoking = promptForYesNo("Does the venue allow smoking? (yes/no): ");

                // ticket-specific details only needed for ticketed events
                int    numTicketsTotal = 0;
                double ticketPrice     = 0.0;

                if (event.getIsTicketed()) {
                    numTicketsTotal = promptForPositiveInt("Enter total number of tickets: ");
                    ticketPrice     = promptForPositiveDouble("Enter ticket price (£): ");
                }

                // create the performance via the event
                newPerformance = event.createPerformance(
                        this.nextPerformanceID, startDateTime, endDateTime,
                        performerNames, venueAddress, venueCapacity,
                        venueOutdoors, venueAllowsSmoking, numTicketsTotal, ticketPrice);

                addPerformance(newPerformance);
                this.nextPerformanceID++;

                view.displaySuccess("Performance added successfully with ID: "
                        + newPerformance.getPerformanceId());
                return newPerformance;

            } catch (NumberFormatException e) {
                view.displayError("Invalid event ID format. Please try again.");
            }
        }
    }

    // helpers for addPerformance

    // helper: prompt for a valid date-time in dd/MM/yyyy HH:mm format
    private LocalDateTime promptForValidDateTime(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        while (true) {

            String input = view.getInput(prompt).trim();

            try {
                return LocalDateTime.parse(input, formatter);

            } catch (DateTimeParseException e) {
                view.displayError("Invalid date/time format. Please use dd/MM/yyyy HH:mm.");
            }
        }
    }

    // helper: prompt for a yes/no answer, returning the corresponding boolean
    private boolean promptForYesNo(String prompt) {

        while (true) {

            String input = view.getInput(prompt).trim().toLowerCase();

            if (input.equals("yes")) {
                return true;
            } else if (input.equals("no")) {
                return false;
            } else {
                view.displayError("Please enter 'yes' or 'no'.");
            }
        }
    }

    // helper: prompt for a positive integer
    private int promptForPositiveInt(String prompt) {

        while (true) {

            String input = view.getInput(prompt).trim();

            try {
                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

                view.displayError("Value must be greater than zero. Please try again.");

            } catch (NumberFormatException e) {
                view.displayError("Invalid number format. Please try again.");
            }
        }
    }

    // helper: prompt for a positive double
    private double promptForPositiveDouble(String prompt) {

        while (true) {

            String input = view.getInput(prompt).trim();

            try {
                double value = Double.parseDouble(input);

                if (value > 0) {
                    return value;
                }

                view.displayError("Value must be greater than zero. Please try again.");

            } catch (NumberFormatException e) {
                view.displayError("Invalid number format. Please try again.");
            }
        }
    }

    public Booking bookPerformance() {

        // precondition: must be a student
        if (!checkCurrentUserIsStudent()) {
            view.displayError("Only students can book performances.");
            return null;
        }

        // get and validate performance ID
        Performance performance = null;

        while (true) {

            String input = view.getInput(
                    "Enter performance ID to book or type 'X' to exit: ").trim();

            if (input.equalsIgnoreCase("X")) {
                return null;
            }

            try {
                long perfID = Long.parseLong(input);
                performance = getPerformanceByID(perfID);

                if (performance == null) {
                    view.displayError("No performance found with that ID. Please try again.");
                    continue;
                }

                // extension 1a: check performance has not been cancelled
                if (performance.getStatus() == PerformanceStatus.CANCELLED) {
                    view.displayError("That performance has been cancelled.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                view.displayError("Invalid ID format. Please try again.");
            }
        }

        Student student  = (Student) this.currentUser;
        Booking booking;

        if (performance.checkIfEventIsTicketed()) {

            // get number of tickets
            int numTickets = 0;

            while (true) {

                String numInput = view.getInput("Enter number of tickets: ").trim();

                if (numInput.equalsIgnoreCase("X")) {
                    return null;
                }

                try {
                    numTickets = Integer.parseInt(numInput);

                    if (numTickets <= 0) {
                        view.displayError("Number of tickets must be positive.");
                        continue;
                    }

                    // extension 2a: check enough tickets are available
                    if (!performance.checkIfTicketsLeft(numTickets)) {
                        view.displayError("Not enough tickets available. "
                                + "Remaining: "
                                + (performance.getNumTicketsTotal()
                                   - performance.getNumTicketsSold()));
                        continue;
                    }

                    break;

                } catch (NumberFormatException e) {
                    view.displayError("Invalid number format. Please try again.");
                }
            }

            double totalCost     = numTickets * performance.getFinalTicketPrice();
            String eventTitle    = performance.getEvent().getEventTitle();
            String studentEmail  = student.getEmail();
            int    studentPhone  = student.getPhoneNumber();
            String epEmail       = performance.getOrganiserEmail();

            boolean paymentSuccess = paymentSystem.processPayment(
                    numTickets, eventTitle, studentEmail, studentPhone, epEmail, totalCost);

            if (!paymentSuccess) {
                // record the failed booking for audit purposes
                booking = new Booking(student, performance, this.nextBookingID,
                        numTickets, 0.0, LocalDateTime.now());
                booking.cancelPaymentFailed();
                this.nextBookingID++;
                view.displayError("Payment failed. Booking was not completed.");
                return null;
            }

            booking = new Booking(student, performance, this.nextBookingID,
                    numTickets, totalCost, LocalDateTime.now());

        } else {
            // non-ticketed: register attendance without payment
            booking = new Booking(student, performance, this.nextBookingID,
                    0, 0.0, LocalDateTime.now());
        }

        this.nextBookingID++;
        performance.addBooking(booking);
        student.addBooking(booking);

        view.displayBookingRecord(booking.generateBookingRecord());
        return booking;
    }

    public void cancelBooking() {

        // precondition: must be a student
        if (!checkCurrentUserIsStudent()) {
            view.displayError("Only students can cancel bookings.");
            return;
        }

        Student student = (Student) this.currentUser;
        Collection<Booking> myBookings = student.getMyBookings();

        // collect only active bookings for display
        List<Booking> activeBookings = new ArrayList<>();

        for (Booking b : myBookings) {

            if (b.getStatus() == BookingStatus.ACTIVE) {
                activeBookings.add(b);
            }
        }

        if (activeBookings.isEmpty()) {
            view.displayError("You have no active bookings to cancel.");
            return;
        }

        // display the student's active bookings
        view.displaySuccess("Your active bookings:");

        for (Booking b : activeBookings) {
            view.displaySuccess(b.generateBookingRecord());
        }

        // get and validate the booking number to cancel
        while (true) {

            String input = view.getInput(
                    "Enter booking number to cancel or type 'X' to exit: ").trim();

            if (input.equalsIgnoreCase("X")) {
                return;
            }

            try {
                long bookingNum = Long.parseLong(input);
                Booking target  = null;

                for (Booking b : activeBookings) {

                    if (b.getBookingNumber() == bookingNum) {
                        target = b;
                        break;
                    }
                }

                if (target == null) {
                    view.displayError("No active booking found with that number. Please try again.");
                    continue;
                }

                // process refund if the event was ticketed
                if (target.getPerformance().checkIfEventIsTicketed()
                        && target.getAmountPaid() > 0) {

                    Performance perf       = target.getPerformance();
                    String      eventTitle = perf.getEvent().getEventTitle();
                    String      epEmail    = perf.getOrganiserEmail();

                    boolean refundSuccess = paymentSystem.processRefund(
                            target.getNumTickets(), eventTitle,
                            student.getEmail(), student.getPhoneNumber(),
                            epEmail, target.getAmountPaid(), "");

                    if (!refundSuccess) {
                        view.displayError("Refund processing failed. Booking was not cancelled.");
                        return;
                    }
                }

                target.cancelbyStudent();
                view.displaySuccess("Booking " + bookingNum + " cancelled successfully.");
                return;

            } catch (NumberFormatException e) {
                view.displayError("Invalid booking number format. Please try again.");
            }
        }
    }


    public void reviewPerformance() {

        // precondition: must be a student
        if (!checkCurrentUserIsStudent()) {
            view.displayError("Only students can review performances.");
            return;
        }

        // get and validate the performance ID
        Performance performance = null;

        while (true) {

            String input = view.getInput(
                    "Enter performance ID to review or type 'X' to exit: ").trim();

            if (input.equalsIgnoreCase("X")) {
                return;
            }

            try {
                long perfID = Long.parseLong(input);
                performance = getPerformanceByID(perfID);

                if (performance == null) {
                    view.displayError("No performance found with that ID. Please try again.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                view.displayError("Invalid ID format. Please try again.");
            }
        }

        // check the student has (or had) a booking for this performance
        Student student = (Student) this.currentUser;
        boolean hasBooking = false;

        for (Booking b : student.getMyBookings()) {

            if (b.getPerformance().getPerformanceId() == performance.getPerformanceId()) {
                hasBooking = true;
                break;
            }
        }

        if (!hasBooking) {
            view.displayError("You can only review performances you have booked.");
            return;
        }

        // get rating (1–5)
        int rating = 0;

        while (true) {

            String ratingInput = view.getInput("Enter rating (1-5): ").trim();

            try {
                rating = Integer.parseInt(ratingInput);

                if (rating >= 1 && rating <= 5) {
                    break;
                }

                view.displayError("Rating must be between 1 and 5.");

            } catch (NumberFormatException e) {
                view.displayError("Invalid input. Please enter a number between 1 and 5.");
            }
        }

        // get written comment
        String comment = "";

        while (true) {

            comment = view.getInput("Enter your review comment: ").trim();

            if (comment.isEmpty()) {
                view.displayError("Review comment cannot be empty.");
            } else {
                break;
            }
        }

        performance.review(rating, comment);
        view.displaySuccess("Review submitted successfully. Thank you for your feedback!");
    }


    private void addEvent(Event e) {
        allEvents.add(e);
    }

    private void addPerformance(Performance p) {
        allPerformances.add(p);
    }

    private Event getEventByID(long eventID) {

        for (Event event : allEvents) {

            if (event.getEventID() == eventID) {

                return event;
            }
        }
        return null;
    }

    private Event getEventByTitle(String title) {

        for (Event event : allEvents) {

            if (event.getEventTitle().equalsIgnoreCase(title)) {

                return event;
            }
        }
        return null;
    }

    private Performance getPerformanceByID(long performanceID) {

        for (Performance performance : allPerformances) {

            if (performance.getPerformanceId() == performanceID) {

                return performance;
            }
        }
        return null;
    }
}
