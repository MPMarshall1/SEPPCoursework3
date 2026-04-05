package com.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookingController extends Controller {

    private Collection<Booking> allBookings;
    private Collection<Performance> allPerformances;
    PaymentSystem paymentSystem = new MockPaymentSystem();

    private long nextBookingID = 1;

    UserController userController;

    public BookingController(UserController userController) {
        super();
        allBookings = new ArrayList<>();
        allPerformances = new ArrayList<>();

        this.userController = userController;
    }

    //Constructor allowing the injection of a custom view
    public BookingController(UserController userController, View view) {
        super(view);
        allBookings = new ArrayList<>();
        allPerformances = new ArrayList<>();

        this.userController = userController;
    }

    public Booking bookPerformance(Collection<Performance> performanceList) {

        // precondition: must be a student
        if (!userController.checkCurrentUserIsStudent()) {
            view.displayError("Only students can book performances.");
            return null;
        }

        // get and validate performance ID
        Performance performance = null;

        this.allPerformances = performanceList;

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

        Student student = (Student) userController.currentUser;
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

            double totalCost = numTickets * performance.getFinalTicketPrice();
            String eventTitle = performance.getEvent().getEventTitle();
            String studentEmail = student.getEmail();
            int studentPhone = student.getPhoneNumber();
            String epEmail = performance.getOrganiserEmail();

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
        allBookings.add(booking);

        view.displayBookingRecord(booking.generateBookingRecord());
        return booking;
    }

    public void reviewPerformance() {

        // precondition: must be a student
        if (!userController.checkCurrentUserIsStudent()) {
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
        Student student = (Student) userController.currentUser;
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

    public void cancelBooking() {

        // precondition: must be a student
        if (!userController.checkCurrentUserIsStudent()) {
            view.displayError("Only students can cancel bookings.");
            return;
        }

        Student student = (Student) userController.currentUser;
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
                Booking target = null;

                target = getBookingByNumber(bookingNum);

                if (target == null) {
                    view.displayError("No active booking found with that number. Please try again.");
                    continue;
                }

                // process refund if the event was ticketed
                if (target.getPerformance().checkIfEventIsTicketed()
                        && target.getAmountPaid() > 0) {

                    Performance perf = target.getPerformance();
                    String eventTitle = perf.getEvent().getEventTitle();
                    String epEmail = perf.getOrganiserEmail();

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

    private Performance getPerformanceByID(long performanceID) {

        for (Performance performance : allPerformances) {
            if (performance.getPerformanceId() == performanceID) {

                return performance;
            }
        }
        return null;
    }

    private Boolean checkIfBookingPossible(Performance performance, int numTickets) {
        if (performance.getStatus() == PerformanceStatus.CANCELLED) {
            view.displayError("That performance has been cancelled.");
            return false;
        }

        int remaining = performance.getNumTicketsTotal() - performance.getNumTicketsSold();
        if (numTickets >= remaining) {
            view.displayError("There are not enough tickets left.");
            return false;
        }

        return true;
    }

    private Collection<Booking> findBookingsByEventID(long eventID) {
        Collection<Booking> matches = new ArrayList<>();

        for (Booking b : allBookings) {
            if (b.getPerformance().getEvent().getEventID() == eventID) {
                matches.add(b);
            }
        }
        return matches;
    }

    private Booking getBookingByNumber(long bookingNumber) {
        for (Booking b : allBookings) {
            if (b.getBookingNumber() == bookingNumber) {
                return b;
            }
        }
        return null;
    }
}