package com.example;

import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestPerformance {

    private Performance testPerformance;
    private Student testStudent;
    private Event testEvent;

    @BeforeEach
    public void setup() {

        EntertainmentProvider testOrganiser = new EntertainmentProvider("organiser@gmail.com", "org123", "Events Organisation", "EORG123", "David Brown", "Event Organiser");
        testEvent = new Event(testOrganiser, 100L, "Event 1", EventType.Sports, true);
        testStudent = new Student("student1@gmail.com", "password123", "Andrew Smith", 123456789);

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = start.plusHours(5);

        testPerformance = new Performance(testEvent, 200L, start, end, new ArrayList<>(),
                "Venue Spot", 200, false, false,
                            200, 10);
    }

    // tests for checkIfTicketsLeft(int numTicketsToBuy)

    @Test
    public void testCheckIfTicketsLeft_validInput_ReturnTrue() {
        // testing valid input, requesting 5 tickets out of 200
        boolean result = testPerformance.checkIfTicketsLeft(5);
        assertTrue(result, "Should return true when requested tickets are lower than total tickets available");
    }

    @Test
    public void testCheckIfTicketsLeft_sameAsTotalTickets_ReturnTrue() {
        // testing requesting exact number of available tickets left
        boolean result = testPerformance.checkIfTicketsLeft(200);
        assertTrue(result, "Should return true when requested tickets is the same as total tickets available");
    }

    @Test
    public void testCheckIfTicketsLeft_higherThanTotal_ReturnFalse() {
        // testing invalid input, requesting more tickets than available
        boolean result = testPerformance.checkIfTicketsLeft(205);
        assertFalse(result, "Should return false when requested tickets are higher than total tickets available");
    }

    @Test
    public void testCheckIfTicketsLeft_negativeInput_ReturnFalse() {
        // testing invalid input, requesting a negative amount of tickets
        boolean result = testPerformance.checkIfTicketsLeft(-5);
        assertFalse(result, "Should return false when requested tickets number is negative");
    }

    // tests for hasActiveBookings()

    @Test
    public void testHasActiveBookings_noBookings_ReturnFalse() {
        // testing when performance has zero bookings
        boolean result = testPerformance.hasActiveBookings();
        assertFalse(result, "Should return false when booking list is empty");
    }

    @Test
    public void testHasActiveBookings_hasActiveBooking_ReturnTrue() {
        // testing when there is at least one active booking made
        Booking activeBooking = new Booking(testStudent, testPerformance, 1L, 3, 30, LocalDateTime.now());
        testPerformance.addBooking(activeBooking);

        boolean result = testPerformance.hasActiveBookings();
        assertTrue(result, "Should return true when there is at least one active booking on list");
    }

    @Test
    public void testHasActiveBookings_cancelledBooking_ReturnFalse() {
        // testing when all bookings on list are not active
        Booking cancelledBooking = new Booking(testStudent, testPerformance, 1L, 3, 30, LocalDateTime.now());
        cancelledBooking.cancelbyStudent();

        boolean result = testPerformance.hasActiveBookings();
        assertFalse(result, "Should return false when no bookings on list are active");
    }

    // tests for checkHasNotHappenedYet

    @Test
    public void testCheckHasNotHappenedYet_eventNotHappenedYet_ReturnTrue() {

        // create specific date in the future
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);

        // create performance with this specific date
        Performance futurePerformance = new Performance(testEvent, 200L, futureDate, futureDate.plusHours(5), new ArrayList<>(),
                "Venue Spot", 200, false, false,
                200, 10);

        boolean result = futurePerformance.checkHasNotHappenedYet();
        assertTrue(result, "Should return true when start date is after current date");
    }

    @Test
    public void testCheckHasNotHappenedYet_eventAlreadyHappened_ReturnFalse() {

        // create specific date in the future
        LocalDateTime pastDate = LocalDateTime.now().minusSeconds(1);

        // create performance with this specific date
        Performance pastPerformance = new Performance(testEvent, 200L, pastDate, pastDate.plusHours(5), new ArrayList<>(),
                "Venue Spot", 200, false, false,
                200, 10);

        boolean result = pastPerformance.checkHasNotHappenedYet();
        assertFalse(result, "Should return false when start date is before current date");
    }

    @Test
    public void testCheckHasNotHappenedYet_eventAlreadyHappened2_ReturnFalse() {

        // create specific date in the future
        LocalDateTime pastDate = LocalDateTime.now().minusYears(3);

        // create performance with this specific date
        Performance pastPerformance = new Performance(testEvent, 200L, pastDate, pastDate.plusHours(5), new ArrayList<>(),
                "Venue Spot", 200, false, false,
                200, 10);

        boolean result = pastPerformance.checkHasNotHappenedYet();
        assertFalse(result, "Should return false when start date is before current date");
    }

    // tests for getBookingDetailsForRefund()

    @Test
    public void testGetBookingDetailsForRefund_noBookings_ReturnsEmptyString() {

        String bookingDetails = testPerformance.getBookingDetailsForRefund();
        assertEquals("", bookingDetails, "Should return an empty string when there are no active bookings");

    }

    @Test
    public void testGetBookingDetailsForRefund_activeBooking_ReturnsDetails() {

        Booking activeBooking = new Booking(testStudent, testPerformance, 1L, 2, 20, LocalDateTime.now());

        testPerformance.addBooking(activeBooking);

        String bookingDetails = testPerformance.getBookingDetailsForRefund();

        String expectedDetails = "Student Name: Andrew Smith, Student Email: student1@gmail.com, Student Number: 123456789" +
                                " | Ticket amount: 2 | Total refund amount: £20.0\n";

        assertEquals(expectedDetails, bookingDetails, "Should return the string in the required format, containing the active booking details");

    }

    @Test
    public void testGetBookingDetailsForRefund_onlyCancelledBookings_ReturnsEmptyString() {

        Booking cancelledBooking = new Booking(testStudent, testPerformance, 1L, 2, 20, LocalDateTime.now());
        cancelledBooking.cancelbyStudent();

        testPerformance.addBooking(cancelledBooking);

        String cancelledBookingDetails = testPerformance.getBookingDetailsForRefund();

        assertEquals("", cancelledBookingDetails, "Should return an empty string when only bookings on list are cancelled");

    }

    @Test
    public void testGetBookingDetailsForRefund_mixedBookings_ReturnsActiveBookingDetails() {

        // set up active booking
        Booking activeBooking = new Booking(testStudent, testPerformance, 1L, 2, 20, LocalDateTime.now());

        // set up cancelled booking
        Student cancelledStudent = new Student("student2@gmail.com,", "password456", "Alice Brown", 987654321);
        Booking cancelledBooking = new Booking(cancelledStudent, testPerformance, 2L, 3, 30, LocalDateTime.now());
        cancelledBooking.cancelbyStudent();

        // adding bookings to list
        testPerformance.addBooking(activeBooking);
        testPerformance.addBooking(cancelledBooking);

        String bookingDetails = testPerformance.getBookingDetailsForRefund();

        String expectedDetails = "Student Name: Andrew Smith, Student Email: student1@gmail.com, Student Number: 123456789" +
                " | Ticket amount: 2 | Total refund amount: £20.0\n";

        assertEquals(expectedDetails, bookingDetails, "Should only return the details of the active booking on the list");
    }

    // test cancel method

    @Test
    public void testCancel_changesStatusToCancelled() {

        testPerformance.cancel();

        assertEquals(PerformanceStatus.CANCELLED, testPerformance.getStatus(), "Performance status should be CANCELLED after using cancel method");
    }

    // testing sponsor method

    @Test
    public void testSponsor_validAmount_updatesVariables() {

        testPerformance.sponsor(5.0);

        assertTrue(testPerformance.getIsSponsored(), "isSponsored should be true when a valid sponsor it made");
        assertEquals(5.0, testPerformance.getSponsoredAmount(), "sponsoredAmount should now contain the inputted sponsor amount");
    }

    @Test
    public void testSponsor_negativeAmount_variablesNotUpdated() {

        testPerformance.sponsor(-5.0);

        assertFalse(testPerformance.getIsSponsored(), "isSponsored should remain false when sponsor amount it negative");
        assertEquals(0.0, testPerformance.getSponsoredAmount(), "sponsoredAmount should remain 0.0 if sponsor amount is negative");
    }

    @Test
    public void testSponsor_sponsorHigherThanTicketPrice_variablesNotUpdated() {

        testPerformance.sponsor(20.0);

        assertFalse(testPerformance.getIsSponsored(), "isSponsored should remain false when sponsor amount exceeds ticket price");
        assertEquals(0.0, testPerformance.getSponsoredAmount(), "sponsoredAmount should remain 0.0 if sponsor amount exceeds ticket price");
    }

    @Test
    public void testSponsor_sponsorIs0_variablesNotUpdated() {

        testPerformance.sponsor(0.0);

        assertFalse(testPerformance.getIsSponsored(), "isSponsored should remain false when sponsor amount is 0");
        assertEquals(0.0, testPerformance.getSponsoredAmount(), "sponsoredAmount should remain 0.0 if sponsor amount is 0");
    }


    @Test
    public void testSponsor_sponsorSameAsTicketPrice_updatesVariables() {

        testPerformance.sponsor(10.0);

        assertTrue(testPerformance.getIsSponsored(), "isSponsored should be true when sponsor amount is the same as the ticket price");
        assertEquals(10.0, testPerformance.getSponsoredAmount(), "sponsoredAmount should now contain the inputted sponsor amount");
    }

    // test addBooking

    @Test
    public void testAddBooking_updateBookingListAndTicketsSold() {

        Booking testBooking = new Booking(testStudent, testPerformance, 1L, 5, 50, LocalDateTime.now());
        testPerformance.addBooking(testBooking);

        assertEquals(1, testPerformance.getBookings().size(), "Booking list size number should now be 1 after adding booking");
        assertEquals(5, testPerformance.getNumTicketsSold(), "Tickets Sold should be 5 after adding booking");
    }

    @Test
    public void testAddBooking_multipleBookings_updateBookingListAndTicketsSold() {

        Booking testBooking = new Booking(testStudent, testPerformance, 1L, 5, 50, LocalDateTime.now());
        testPerformance.addBooking(testBooking);

        Student secondStudent = new Student("testStudent@gmail.com", "pass123", "John Wilson", 987654321);
        Booking secondBooking = new Booking(secondStudent, testPerformance, 2L, 2, 20, LocalDateTime.now());
        testPerformance.addBooking(secondBooking);

        assertEquals(2, testPerformance.getBookings().size(), "Booking list size number should now be 2 after adding bookings");
        assertEquals(7, testPerformance.getNumTicketsSold(), "Tickets Sold should be 7 after adding bookings");
    }

    // testing review

    @Test
    public void testReview_oneReview_updateLists() {

        testPerformance.review(5, "Good performance");

        // testing arrays size
        assertEquals(1, testPerformance.getReviewRatings().size(), "Review ratings list size should be one after adding review");
        assertEquals(1, testPerformance.getReviewComments().size(), "Review comments list size should be one after adding review");

        // testing reviews details
        assertTrue(testPerformance.getReviewRatings().contains(5), "Review ratings should now contain the rating received");
        assertTrue(testPerformance.getReviewComments().contains("Good performance"), "Review comments should now contain the comment received");
    }

    @Test
    public void testReview_multipleReviews_updateLists() {

        testPerformance.review(5, "Good performance");
        testPerformance.review(3, "Needs improvement");

        // testing arrays size
        assertEquals(2, testPerformance.getReviewRatings().size(), "Review ratings list size should be two after adding reviews");
        assertEquals(2, testPerformance.getReviewComments().size(), "Review comments list size should be two after adding reviews");

        // testing first review details
        assertTrue(testPerformance.getReviewRatings().contains(5), "Review ratings should now contain the rating received");
        assertTrue(testPerformance.getReviewComments().contains("Good performance"), "Review comments should now contain the comment received");

        // testing second review details
        assertTrue(testPerformance.getReviewRatings().contains(3), "Review ratings should now contain the rating received");
        assertTrue(testPerformance.getReviewComments().contains("Needs improvement"), "Review comments should now contain the comment received");
    }

    // testing checkIfEventIsTicketed

    @Test
    public void testCheckIfEventIsTicketed_ReturnsTrue() {

        assertTrue(testPerformance.checkIfEventIsTicketed(), "Should return true since testEvent is ticketed");
    }

    @Test
    public void testCheckIfEventIsTicketed_ReturnsFalse() {

        // creating new event that is not ticketed
        EntertainmentProvider testOrganiser2 = new EntertainmentProvider("organiser2@gmail.com", "oRg123", "Other Organisation", "EORG321", "Daniel Brown", "Non Ticketed Event Organiser");
        Event nonTicketedEvent = new Event(testOrganiser2,200L, "Event 2", EventType.Music, false);

        LocalDateTime start2 = LocalDateTime.now().plusDays(3);
        LocalDateTime end2 = start2.plusHours(3);

        Performance testPerformance2 = new Performance(nonTicketedEvent, 300L, start2, end2, new ArrayList<>(),
                "Venue Spot", 100, false, false,
                0, 0);

        assertFalse(testPerformance2.checkIfEventIsTicketed(), "Should return false since event is not ticketed");
    }

    // testing checkCreatedByEP

    @Test
    public void testCheckCreatedByEP_ReturnTrue() {

        assertTrue(testPerformance.checkCreatedByEP("organiser@gmail.com"), "Should return true when email matches with organisers email");
    }

    @Test
    public void testCheckCreatedByEP_ReturnFalse() {

        assertFalse(testPerformance.checkCreatedByEP("othername@gmail.com"), "Should return false when email doesn't match with organisers email");
    }

    // testing toString

    @Test
    public void testToString_containsMainInformation() {

        String details = testPerformance.toString();

        assertTrue(details.contains("Performance ID:"), "Should contain performance ID");
        assertTrue(details.contains("Event:"), "Should contain event title");
        assertTrue(details.contains("Date:"), "Should contain performance start and end times");
        assertTrue(details.contains("Venue:"), "Should contain venue name");
        assertTrue(details.contains("Price: £10.0"), "Should contain ticket price");
    }
}
