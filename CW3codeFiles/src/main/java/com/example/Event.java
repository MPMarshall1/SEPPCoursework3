package com.example;

import java.text.CollationElementIterator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Event {

    EntertainmentProvider organiser;
    private long eventID;
    private String title;
    private EventType type;
    private boolean isTicketed;

    Collection<Performance> performances;

    public Event(EntertainmentProvider organiser, long eventID, String title, EventType type, boolean isTicketed) {
        this.organiser = organiser;
        this.eventID = eventID;
        this.title = title;
        this.type = type;
        this.isTicketed = isTicketed;

        this.performances = new ArrayList<>();
    }

    public Performance createPerformance(long performanceId, LocalDateTime startDateTime,
                                         LocalDateTime endDateTime, Collection<String> performerNames,
                                         String venueAddress, int venueCapacity, boolean venueOutdoors,
                                         boolean venueAllowsSmoking, int numTicketsTotal, double ticketPrice) {

        Performance newPerformance = new Performance(this, performanceId, startDateTime,
                                                     endDateTime, performerNames, venueAddress,
                                                     venueCapacity, venueOutdoors, venueAllowsSmoking,
                                                     numTicketsTotal, ticketPrice);

        performances.add(newPerformance);

        return newPerformance;
    }

    private void addPerformance(Performance p) {

        performances.add(p);
    }

    public Performance getPerformanceByID(long performanceID) {

        for (Performance performance : performances) {

            if (performance.getPerformanceId() == performanceID) {

                return performance;
            }
        }
        return null;
    }


    public Collection<String> getInfoOfPerformancesOnDate(LocalDateTime searchDateTime) {

        Collection<String> performancesOnDate = new ArrayList<>();

        for (Performance performance : performances) {

            if (performance.getStartDateTime().toLocalDate().equals(searchDateTime.toLocalDate())) {

                performancesOnDate.add(performance.toString());
            }
        }

        return performancesOnDate;
    }

    public double getAverageRatingOfPerformances() {

        if (performances.isEmpty()) {

            return 0.0;
        }

        double totalRating = 0; // final number of ratings across performances
        int reviewAmount = 0;   // total number of reviews left

        for (Performance performance : performances) {

           for (Integer rating : performance.getReviewRatings()) {

               totalRating += rating;
               reviewAmount ++; // keeps count of total number of reviews
           }
        }

        if (reviewAmount == 0) {
            return 0.0;
        }

        return totalRating / reviewAmount;  // return average rating
    }

    public Collection<String> getAllPerformanceReviews() {

        Collection<String> allReviews = new ArrayList<>();

        for (Performance performance : performances) {

            long perfID = performance.getPerformanceId();

            // use iterators to go through both list at the same time
            Iterator<Integer> ratingIterator = performance.getReviewRatings().iterator();
            Iterator<String> commentIterator = performance.getReviewComments().iterator();

            // while there is still reviews left
            while (ratingIterator.hasNext() && commentIterator.hasNext()) {

                int rating = ratingIterator.next();
                String comment = commentIterator.next();

                // combine the three parts
                String review = perfID + " - " + rating + " - " + comment;
                allReviews.add(review);
            };
        }
        return allReviews;
    }

    private boolean hasPerformanceAtSameTimes(LocalDateTime startDateTime, LocalDateTime endDateTime) {

        // loop through all existing performances
        for (Performance performance : performances) {

            // checks whether given times overlap with current performance times
            boolean startsBeforePerfEnds = startDateTime.isBefore(performance.getEndDateTime());
            boolean endsAfterPerfStarts = endDateTime.isAfter(performance.getStartDateTime());

            // if both true then there is a clash
            if (startsBeforePerfEnds && endsAfterPerfStarts) {
                return true;
            }
        }
        return false;
    }

    // getters

    public String getOrganiserEmail() {
        return this.organiser.getEmail();
    }

    // changed from private to public as controller needs to show details of organiser name
    public String getOrganiserName() {
        return this.organiser.getOrgName();
    }

    public String getEventTitle() {
        return this.title;
    }

    public boolean getIsTicketed() {
        return this.isTicketed;
    }

    public long getEventID() {
        return eventID;
    }

    public EventType getEventType() {
        return type;
    }

    @Override
    public String toString() {

        return "Event Details:\n" +
                "ID: " + this.eventID + "\n" +
                "Title: " + this.title + "\n" +
                "Type: " + this.type + "\n" +
                "Ticketed: " + this.isTicketed + "\n" +
                "Organiser Name: " + this.getOrganiserName() + "\n" +
                "Organiser Email: " + this.getOrganiserEmail() + "\n" +
                "Total Performances: " + this.performances.size() + "\n";
    }
}
