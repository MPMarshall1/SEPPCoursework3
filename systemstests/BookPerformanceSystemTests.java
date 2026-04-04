package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookPerformanceSystemTests extends BaseSystemTest {

    @Test
    void studentShouldBeAbleToBookTicketedPerformanceWhenPaymentSucceeds() {
        // student successfully books a ticketed performance when the payment succeeds
        createEventWithPerformanceOwnedByProvider();
        paymentSystem.setNextPaymentResult(true);
        userController.login("student@gmail.com", "pw123");

        Booking booking = bookingController.bookPerformance(student, getOnlyPerformance(), 2);

        assertNotNull(booking, "Booking should be created when payment succeeds");
        assertEquals(BookingStatus.ACTIVE, booking.getStatus(), "Successful booking should be ACTIVE");
        assertEquals(2, booking.getNumTickets(), "Booking should store requested ticket quantity");
    }

    @Test
    void bookingUnticketedPerformanceShouldFail() {
        // booking should fail for the unticketed performances
        Event event = new Event(100L, "Free Fair", EventType.Music, provider);
        Performance performance = new Performance(201L, "Free Fair", event, 0.0, 100, false);
        eventPerformanceController.addEvent(event);
        eventPerformanceController.addPerformance(performance);
        userController.login("student@gmail.com", "pw123");

        Booking booking = bookingController.bookPerformance(student, performance, 1);

        assertNull(booking, "Booking should fail for unticketed performances");
    }

    @Test
    void bookingWithTooManyTicketsShouldFail() {
        // booking should fail if requested tickets is sold out
        createEventWithPerformanceOwnedByProvider();
        userController.login("student@gmail.com", "pw123");

        Booking booking = bookingController.bookPerformance(student, getOnlyPerformance(), 1000);

        assertNull(booking, "Booking should fail when requested ticket count exceeds availability");
    }

    @Test
    void bookingShouldBeMarkedPaymentFailedWhenPaymentFails() {
        // booking should be created but marked as failed when payment fails
        createEventWithPerformanceOwnedByProvider();
        paymentSystem.setNextPaymentResult(false);
        userController.login("student@gmail.com", "pw123");

        Booking booking = bookingController.bookPerformance(student, getOnlyPerformance(), 2);

        assertNotNull(booking, "A booking object may still be created for failed payment depending on design");
        assertEquals(BookingStatus.PAYMENTFAILED, booking.getStatus(),
                "Booking status should record failed payment");
    }
}