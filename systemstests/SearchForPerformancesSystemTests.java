package systemstests;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SearchForPerformancesSystemTests extends BaseSystemTest {

    @Test
    void searchShouldReturnMatchingPerformanceByTitleKeyword() {
        // search should return a performances according to the matching the keywords
        createEventWithPerformanceOwnedByProvider();

        Collection<Performance> results = eventPerformanceController.searchForPerformances("Opera");

        assertEquals(1, results.size(), "Search should return one matching performance");
    }

    @Test
    void searchWithNoMatchesShouldReturnEmptyCollection() {
        // search with no matching keywords should return an empty result
        createEventWithPerformanceOwnedByProvider();

        Collection<Performance> results = eventPerformanceController.searchForPerformances("Formula1");

        assertTrue(results.isEmpty(), "Search should return empty collection when nothing matches");
    }
}