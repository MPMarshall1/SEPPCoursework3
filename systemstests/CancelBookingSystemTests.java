package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancelBookingSystemTests extends BaseSystemTest {

    @Test
    void studentShouldBeAbleToCancelOwnActiveBooking() {
        // student cancels their booking
        createEventWithPerformanceOwnedByProvider();
        paymentSystem.setNextPaymentResult(true);
        userController.login("student@gmail.com", "pw123");
        Booking booking = bookingController.bookPerformance(student, getOnlyPerformance(), 2);

        boolean cancelled = bookingController.cancelBooking(student, booking);

        assertTrue(cancelled, "Student should be able to cancel their own active booking");
        assertEquals(BookingStatus.CANCELLEDBYSTUDENT, booking.getStatus(),
                "Cancelled booking should record student cancellation");
    }

    @Test
    void studentShouldNotBeAbleToCancelAnotherStudentsBooking() {
        // student tries to cancel other students booking should not be canceled
        Student otherStudent = new Student("student2@gmail.com", "pw456", "Student Two");
        userController.addUser(otherStudent);
        createEventWithPerformanceOwnedByProvider();
        paymentSystem.setNextPaymentResult(true);
        Booking booking = bookingController.bookPerformance(otherStudent, getOnlyPerformance(), 1);

        boolean cancelled = bookingController.cancelBooking(student, booking);

        assertFalse(cancelled, "Student should not cancel a booking they do not own");
        assertEquals(BookingStatus.ACTIVE, booking.getStatus(),
                "Booking should remain active when another student attempts cancellation");
    }
}