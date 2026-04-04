package com.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class Performance {

    Event event;
    private long performanceId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Collection<String> performerNames;
    private String venueAddress;
    private int venueCapacity;
    private boolean venueOutdoors;
    private boolean venueAllowsSmoking;
    private int numTicketsTotal;
    private int numTicketsSold;
    private double ticketPrice;
    private boolean isSponsored;
    private double sponsoredAmount;
    private Collection<Integer> reviewRatings;
    private Collection<String> reviewComments;
    private Collection<Booking> bookings;
    private PerformanceStatus status;


    public Performance(Event event, long performanceId, LocalDateTime startDateTime, LocalDateTime endDateTime, Collection<String> performerNames, String venueAddress, int venueCapacity, boolean venueOutdoors, boolean venueAllowsSmoking, int numTicketsTotal, double ticketPrice) {
        this.event = event;
        this.performanceId = performanceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.performerNames = performerNames;
        this.venueAddress = venueAddress;
        this.venueCapacity = venueCapacity;
        this.venueOutdoors = venueOutdoors;
        this.venueAllowsSmoking = venueAllowsSmoking;
        this.numTicketsTotal = numTicketsTotal;
        this.ticketPrice = ticketPrice;

        this.isSponsored = false;
        this.sponsoredAmount = 0.0;
        this.status = PerformanceStatus.ACTIVE;
        this.numTicketsSold = 0;
        this.reviewRatings = new ArrayList<>();
        this.reviewComments = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }


    public void sponsor(double amount) {

        if (amount > 0 && amount <= this.ticketPrice) {
            this.isSponsored = true;
            this.sponsoredAmount = amount;
        }
    }

    public void cancel() {
        this.status = PerformanceStatus.CANCELLED;
    }

    public boolean checkIfTicketsLeft(int numTicketsToBuy) {

        if (numTicketsToBuy < 0) {
            return false;
        }

        return this.numTicketsTotal - this.numTicketsSold >= numTicketsToBuy;
    }

    public boolean checkIfEventIsTicketed() {
        return event.getIsTicketed();
    }

    public boolean checkHasNotHappenedYet() {
        LocalDateTime currentTime = LocalDateTime.now();

        return this.startDateTime.isAfter(currentTime);
    }

    // check if email inputted matches event organisers email
    public boolean checkCreatedByEP(String email) {
        return getOrganiserEmail().equals(email);
    }

    public void review(int rating, String comment) {
        this.reviewRatings.add(rating);
        this.reviewComments.add(comment);
    }

    // check these
    public boolean hasActiveBookings() {

        for (Booking booking : this.bookings) {

            if (booking.getStatus() == BookingStatus.ACTIVE) {
                return true;
            }
        }

        return false;
    }

    public void addBooking(Booking b) {
        this.bookings.add(b);

        this.numTicketsSold += b.getNumTickets();
    }

    public String getBookingDetailsForRefund() {
        StringBuilder refundDetails = new StringBuilder();

        for (Booking b : bookings) {

            if (b.getStatus() == BookingStatus.ACTIVE) {

                String studentDetails = b.getStudentDetails();
                double amountPaid = b.getAmountPaid();
                int ticketsNum = b.getNumTickets();

                refundDetails.append(studentDetails)
                        .append(" | Ticket amount: ").append(ticketsNum)
                        .append(" | Total refund amount: £").append(amountPaid)
                        .append("\n");
            }
        }
        return refundDetails.toString();
    }

    @Override
    public String toString() {

        int totalTickets = this.numTicketsTotal - this.numTicketsSold;

        return "Performance Details: \n" +
                "Performance ID: "+ this.performanceId + "\n" +
                "Event: " + this.getEventTitle() +"\n" +
                "Organiser: " + this.getOrganiserEmail() + "\n" +
                "Date: " + this.startDateTime + " to " + this.endDateTime +"\n" +
                "Venue: " + this.venueAddress + " (Capacity: "+ this.venueCapacity + ") \n" +
                "Venue Details: " + "Outdoors("+this.venueOutdoors+"), " + "Smoking("+this.venueAllowsSmoking+") \n" +
                "Tickets: " + totalTickets + " left \n" +
                "Price: £" + this.ticketPrice + "\n" +
                "Status: " + this.status + "\n";
    }

    // getters

    public double getFinalTicketPrice() {

        if (this.isSponsored) {
            return this.ticketPrice - this.sponsoredAmount;
        }
        return this.ticketPrice;
    }

    public String getOrganiserEmail() {
        return event.getOrganiserEmail();
    }

    public String getEventTitle() {
        return event.getEventTitle();
    }

    public Event getEvent() {
        return event;
    }

    public long getPerformanceId() {
        return performanceId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public Collection<String> getPerformerNames() {
        return performerNames;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public int getVenueCapacity() {
        return venueCapacity;
    }

    public boolean isVenueOutdoors() {
        return venueOutdoors;
    }

    public boolean isVenueAllowsSmoking() {
        return venueAllowsSmoking;
    }

    public int getNumTicketsTotal() {
        return numTicketsTotal;
    }

    public int getNumTicketsSold() {
        return numTicketsSold;
    }

    public boolean getIsSponsored() {
        return isSponsored;
    }

    public double getSponsoredAmount() {
        return sponsoredAmount;
    }

    public Collection<Integer> getReviewRatings() {
        return reviewRatings;
    }

    public Collection<String> getReviewComments() {
        return reviewComments;
    }

    public Collection<Booking> getBookings() {
        return bookings;
    }

    public PerformanceStatus getStatus() {
        return status;
    }
}
