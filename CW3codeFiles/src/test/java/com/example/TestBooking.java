package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestBooking {

    private Booking testBooking;
    private Student testStudent;
    private Performance testPerformance;

    @BeforeEach
    public void setup() {

        EntertainmentProvider testOrganiser = new EntertainmentProvider("organiser@gmail.com", "org123", "Events Organisation", "EORG123", "David Brown", "Event Organiser");
        Event testEvent = new Event(testOrganiser, 100L, "Jazz Night", EventType.Music, true);
        testStudent = new Student("student1@gmail.com", "password123", "Andrew Smith", 123456789);

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = start.plusHours(3);

        testPerformance = new Performance(testEvent, 200L, start, end, new ArrayList<>(),
                "Venue Spot", 200, false, false, 200, 10);

        testBooking = new Booking(testStudent, testPerformance, 1L, 3, 30.0, LocalDateTime.now());
    }

    // tests for initial status

    @Test
    public void testBooking_initialStatus_isActive() {
        // testing a newly created booking has ACTIVE status
        assertEquals(BookingStatus.ACTIVE, testBooking.getStatus(), "A newly created booking should have ACTIVE status");
    }

    // tests for cancelbyStudent()

    @Test
    public void testCancelbyStudent_changesStatusToCancelledByStudent() {
        // testing that cancelling by student changes status correctly
        testBooking.cancelbyStudent();
        assertEquals(BookingStatus.CANCELLEDBYSTUDENT, testBooking.getStatus(), "Status should be CANCELLEDBYSTUDENT after student cancels booking");
    }

    @Test
    public void testCancelbyStudent_statusNotActive_AfterCancellation() {
        // testing that status is no longer ACTIVE after student cancels
        testBooking.cancelbyStudent();
        assertNotEquals(BookingStatus.ACTIVE, testBooking.getStatus(), "Status should no longer be ACTIVE after student cancels booking");
    }

    // tests for cancelByProvider()

    @Test
    public void testCancelByProvider_changesStatusToCancelledByProvider() {
        // testing that cancelling by provider changes status correctly
        testBooking.cancelByProvider();
        assertEquals(BookingStatus.CANCELLEDBYPROVIDER, testBooking.getStatus(), "Status should be CANCELLEDBYPROVIDER after provider cancels booking");
    }

    @Test
    public void testCancelByProvider_statusNotActive_AfterCancellation() {
        // testing that status is no longer ACTIVE after provider cancels
        testBooking.cancelByProvider();
        assertNotEquals(BookingStatus.ACTIVE, testBooking.getStatus(), "Status should no longer be ACTIVE after provider cancels booking");
    }

    // tests for cancelPaymentFailed()

    @Test
    public void testCancelPaymentFailed_changesStatusToPaymentFailed() {
        // testing that payment failure changes status correctly
        testBooking.cancelPaymentFailed();
        assertEquals(BookingStatus.PAYMENTFAILED, testBooking.getStatus(), "Status should be PAYMENTFAILED after payment failure");
    }

    @Test
    public void testCancelPaymentFailed_statusNotActive_AfterFailure() {
        // testing that status is no longer ACTIVE after payment failure
        testBooking.cancelPaymentFailed();
        assertNotEquals(BookingStatus.ACTIVE, testBooking.getStatus(), "Status should no longer be ACTIVE after payment failure");
    }

    // tests for checkBookedByStudent()

    @Test
    public void testCheckBookedByStudent_matchingEmail_ReturnTrue() {
        // testing correct email returns true
        boolean result = testBooking.checkBookedByStudent("student1@gmail.com");
        assertTrue(result, "Should return true when email matches the student who made the booking");
    }

    @Test
    public void testCheckBookedByStudent_wrongEmail_ReturnFalse() {
        // testing incorrect email returns false
        boolean result = testBooking.checkBookedByStudent("otherstudent@gmail.com");
        assertFalse(result, "Should return false when email does not match the student who made the booking");
    }

    @Test
    public void testCheckBookedByStudent_emptyEmail_ReturnFalse() {
        // testing empty string email returns false
        boolean result = testBooking.checkBookedByStudent("");
        assertFalse(result, "Should return false when email is an empty string");
    }

    @Test
    public void testCheckBookedByStudent_partialEmail_ReturnFalse() {
        // testing partial email match returns false
        boolean result = testBooking.checkBookedByStudent("student1");
        assertFalse(result, "Should return false when only part of the email is provided");
    }

    // tests for getStudentDetails()

    @Test
    public void testGetStudentDetails_containsStudentName() {
        // testing return string contains student name
        String details = testBooking.getStudentDetails();
        assertTrue(details.contains("Andrew Smith"), "Student details should contain the student's name");
    }

    @Test
    public void testGetStudentDetails_containsStudentEmail() {
        // testing return string contains student email
        String details = testBooking.getStudentDetails();
        assertTrue(details.contains("student1@gmail.com"), "Student details should contain the student's email");
    }

    @Test
    public void testGetStudentDetails_containsStudentPhoneNumber() {
        // testing return string contains student phone number
        String details = testBooking.getStudentDetails();
        assertTrue(details.contains("123456789"), "Student details should contain the student's phone number");
    }

    @Test
    public void testGetStudentDetails_correctFormat() {
        // testing return string matches expected format exactly
        String details = testBooking.getStudentDetails();
        String expected = "Student Name: Andrew Smith, Student Email: student1@gmail.com, Student Number: 123456789";
        assertEquals(expected, details, "Student details should match the expected format");
    }

    // tests for generateBookingRecord()

    @Test
    public void testGenerateBookingRecord_containsBookingNumber() {
        // testing return string contains booking number
        String record = testBooking.generateBookingRecord();
        assertTrue(record.contains("1"), "Booking record should contain the booking number");
    }

    @Test
    public void testGenerateBookingRecord_containsEventTitle() {
        // testing return string contains event title
        String record = testBooking.generateBookingRecord();
        assertTrue(record.contains("Jazz Night"), "Booking record should contain the event title");
    }

    @Test
    public void testGenerateBookingRecord_containsTicketNumber() {
        // testing return string contains number of tickets
        String record = testBooking.generateBookingRecord();
        assertTrue(record.contains("3"), "Booking record should contain the number of tickets");
    }

    @Test
    public void testGenerateBookingRecord_containsAmountPaid() {
        // testing return string contains amount paid
        String record = testBooking.generateBookingRecord();
        assertTrue(record.contains("30.0"), "Booking record should contain the amount paid");
    }
}
