package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SponsorPerformanceSystemTests extends BaseSystemTest {

    @Test
    void adminShouldBeAbleToSponsorTicketedPerformanceWithValidAmount() {
        // the admin successfully sponsors a valid ticketed performance
        createEventWithPerformanceOwnedByProvider();
        userController.login("admin@gmail.com", "adminpw");
        Performance performance = getOnlyPerformance();

        boolean sponsored = eventPerformanceController.sponsorPerformance(performance, 250.0);

        assertTrue(sponsored, "Admin should be able to sponsor eligible performance with valid amount");
        assertTrue(performance.isSponsored(), "Performance should be marked as sponsored");
        assertEquals(250.0, performance.getSponsoredAmount(),
                "Sponsored amount should be stored on the performance");
    }

    @Test
    void sponsorPerformanceShouldFailForUnticketedPerformance() {
        // sponsorship should fail for the unticketed performance
        Event event = new Event(101L, "Open Mic", EventType.Music, provider);
        Performance performance = new Performance(202L, "Open Mic", event, 0.0, 50, false);
        eventPerformanceController.addEvent(event);
        eventPerformanceController.addPerformance(performance);
        userController.login("admin@gmail.com", "adminpw");

        boolean sponsored = eventPerformanceController.sponsorPerformance(performance, 100.0);

        assertFalse(sponsored, "Unticketed performance should not be eligible for sponsorship");
        assertFalse(performance.isSponsored(), "Unticketed performance should remain unsponsored");
    }

    @Test
    void sponsorPerformanceShouldFailWhenAmountIsInvalid() {
        // sponsorship should fail for invalid amount
        createEventWithPerformanceOwnedByProvider();
        userController.login("admin@gmail.com", "adminpw");
        Performance performance = getOnlyPerformance();

        boolean sponsored = eventPerformanceController.sponsorPerformance(performance, -5.0);

        assertFalse(sponsored, "Negative sponsorship amount should be rejected");
        assertFalse(performance.isSponsored(), "Performance should remain unsponsored for invalid amount");
    }
}