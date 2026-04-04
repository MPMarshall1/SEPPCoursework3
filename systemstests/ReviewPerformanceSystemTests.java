package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewPerformanceSystemTests extends BaseSystemTest {

    @Test
    void studentShouldBeAbleToReviewPerformanceTheyAttended() {
        // student books a performance then successfully reviews it
        createEventWithPerformanceOwnedByProvider();
        userController.login("student@gmail.com", "pw123");
        paymentSystem.setNextPaymentResult(true);
        bookingController.bookPerformance(student, getOnlyPerformance(), 1);

        boolean reviewed = bookingController.reviewPerformance(student, getOnlyPerformance(), 5, "Excellent show");

        assertTrue(reviewed, "Student should be able to review a performance they attended");
        assertEquals(1, getOnlyPerformance().getReviews().size(),
                "Performance should contain one recorded review");
    }

    @Test
    void studentShouldNotBeAbleToReviewPerformanceWithoutBooking() {
        // student tries to review without booking the performance results in rejection
        createEventWithPerformanceOwnedByProvider();
        userController.login("student@gmail.com", "pw123");

        boolean reviewed = bookingController.reviewPerformance(student, getOnlyPerformance(), 4, "Good");

        assertFalse(reviewed, "Student should not review a performance they did not book/attend");
    }
}