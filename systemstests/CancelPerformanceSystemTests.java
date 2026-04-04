package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancelPerformanceSystemTests extends BaseSystemTest {

    @Test
    void providerShouldBeAbleToCancelOwnFuturePerformance() {
        // provider cancels their own performance
        createEventWithPerformanceOwnedByProvider();
        Performance performance = getOnlyPerformance();
        userController.login("ep@org.com", "eppw");
        paymentSystem.setNextRefundResult(true);

        boolean cancelled = eventPerformanceController.cancelPerformance(provider, performance, "Venue issue");

        assertTrue(cancelled, "Provider should be able to cancel own future performance");
        assertEquals(PerformanceStatus.CANCELLED, performance.getStatus(),
                "Cancelled performance should have CANCELLED status");
    }

    @Test
    void providerShouldNotBeAbleToCancelAnotherProvidersPerformance() {
        // provider cannot cancel another provider’s performance
        createEventWithPerformanceOwnedByProvider();
        Performance performance = getOnlyPerformance();
        EntertainmentProvider otherProvider = new EntertainmentProvider(
                "other@org.com", "pw", "Other Org", "BN555"
        );

        boolean cancelled = eventPerformanceController.cancelPerformance(otherProvider, performance, "No reason");

        assertFalse(cancelled, "Provider should not be able to cancel a performance they do not own");
        assertEquals(PerformanceStatus.ACTIVE, performance.getStatus(),
                "Performance should remain active after rejected cancellation");
    }

    @Test
    void cancelPerformanceShouldFailIfRefundProcessFails() {
        // cancellation fails if refund process does not succeed
        createEventWithPerformanceOwnedByProvider();
        Performance performance = getOnlyPerformance();
        userController.login("ep@org.com", "eppw");
        paymentSystem.setNextRefundResult(false);

        boolean cancelled = eventPerformanceController.cancelPerformance(provider, performance, "Venue issue");

        assertFalse(cancelled, "Cancellation should fail if refund processing fails in your design");
        assertEquals(PerformanceStatus.ACTIVE, performance.getStatus(),
                "Performance should remain active if cancellation does not complete");
    }
}