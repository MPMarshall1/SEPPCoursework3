package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMockPaymentSystem {

    private MockPaymentSystem mockPaymentSystem;

    // shared valid test data
    private static final int VALID_NUM_TICKETS = 2;
    private static final String VALID_EVENT_TITLE = "Jazz Night";
    private static final String VALID_STUDENT_EMAIL = "student@ed.ac.uk";
    private static final int VALID_STUDENT_PHONE = 123456789;
    private static final String VALID_EP_EMAIL = "ep@events.com";
    private static final double VALID_AMOUNT = 20.0;
    private static final String VALID_ORGANISER_MSG = "Sorry for the inconvenience";

    @BeforeEach
    public void setup() {
        mockPaymentSystem = new MockPaymentSystem();
    }

    // tests for processPayment()

    @Test
    public void testProcessPayment_validInputs_ReturnTrue() {
        // testing all valid inputs returns true
        boolean result = mockPaymentSystem.processPayment(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT);
        assertTrue(result, "Should return true when all inputs are valid");
    }

    @Test
    public void testProcessPayment_nullStudentEmail_ReturnFalse() {
        // testing null student email returns false
        boolean result = mockPaymentSystem.processPayment(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                null, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT);
        assertFalse(result, "Should return false when student email is null");
    }

    @Test
    public void testProcessPayment_nullEpEmail_ReturnFalse() {
        // testing null EP email returns false
        boolean result = mockPaymentSystem.processPayment(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, null, VALID_AMOUNT);
        assertFalse(result, "Should return false when EP email is null");
    }

    @Test
    public void testProcessPayment_nullEventTitle_ReturnFalse() {
        // testing null event title returns false
        boolean result = mockPaymentSystem.processPayment(VALID_NUM_TICKETS, null,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT);
        assertFalse(result, "Should return false when event title is null");
    }

    @Test
    public void testProcessPayment_zeroTickets_ReturnFalse() {
        // testing zero tickets is invalid
        boolean result = mockPaymentSystem.processPayment(0, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT);
        assertFalse(result, "Should return false when number of tickets is zero");
    }

    @Test
    public void testProcessPayment_negativeTickets_ReturnFalse() {
        // testing negative ticket count is invalid
        boolean result = mockPaymentSystem.processPayment(-1, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT);
        assertFalse(result, "Should return false when number of tickets is negative");
    }

    @Test
    public void testProcessPayment_oneTicket_ReturnTrue() {
        // testing boundary: one ticket is valid
        boolean result = mockPaymentSystem.processPayment(1, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT);
        assertTrue(result, "Should return true when number of tickets is one");
    }

    @Test
    public void testProcessPayment_zeroAmount_ReturnFalse() {
        // testing zero transaction amount is invalid
        boolean result = mockPaymentSystem.processPayment(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, 0);
        assertFalse(result, "Should return false when transaction amount is zero");
    }

    @Test
    public void testProcessPayment_negativeAmount_ReturnFalse() {
        // testing negative transaction amount is invalid
        boolean result = mockPaymentSystem.processPayment(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, -10.0);
        assertFalse(result, "Should return false when transaction amount is negative");
    }

    @Test
    public void testProcessPayment_smallPositiveAmount_ReturnTrue() {
        // testing boundary: very small positive amount is valid
        boolean result = mockPaymentSystem.processPayment(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, 0.01);
        assertTrue(result, "Should return true when transaction amount is a small positive value");
    }

    @Test
    public void testProcessPayment_largeTicketCount_ReturnTrue() {
        // testing large ticket count is still valid
        boolean result = mockPaymentSystem.processPayment(500, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, 5000.0);
        assertTrue(result, "Should return true when ticket count is large but valid");
    }

    // tests for processRefund()

    @Test
    public void testProcessRefund_validInputs_ReturnTrue() {
        // testing all valid inputs returns true
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT, VALID_ORGANISER_MSG);
        assertTrue(result, "Should return true when all inputs are valid");
    }

    @Test
    public void testProcessRefund_nullStudentEmail_ReturnFalse() {
        // testing null student email returns false
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                null, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT, VALID_ORGANISER_MSG);
        assertFalse(result, "Should return false when student email is null");
    }

    @Test
    public void testProcessRefund_nullEpEmail_ReturnFalse() {
        // testing null EP email returns false
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, null, VALID_AMOUNT, VALID_ORGANISER_MSG);
        assertFalse(result, "Should return false when EP email is null");
    }

    @Test
    public void testProcessRefund_nullEventTitle_ReturnFalse() {
        // testing null event title returns false
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, null,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT, VALID_ORGANISER_MSG);
        assertFalse(result, "Should return false when event title is null");
    }

    @Test
    public void testProcessRefund_nullOrganiserMsg_ReturnTrue() {
        // testing null organiser message is still valid (optional field)
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT, null);
        assertTrue(result, "Should return true when organiser message is null as it is optional");
    }

    @Test
    public void testProcessRefund_zeroTickets_ReturnFalse() {
        // testing zero tickets is invalid
        boolean result = mockPaymentSystem.processRefund(0, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT, VALID_ORGANISER_MSG);
        assertFalse(result, "Should return false when number of tickets is zero");
    }

    @Test
    public void testProcessRefund_negativeTickets_ReturnFalse() {
        // testing negative ticket count is invalid
        boolean result = mockPaymentSystem.processRefund(-3, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT, VALID_ORGANISER_MSG);
        assertFalse(result, "Should return false when number of tickets is negative");
    }

    @Test
    public void testProcessRefund_oneTicket_ReturnTrue() {
        // testing boundary: one ticket is valid
        boolean result = mockPaymentSystem.processRefund(1, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, VALID_AMOUNT, VALID_ORGANISER_MSG);
        assertTrue(result, "Should return true when number of tickets is one");
    }

    @Test
    public void testProcessRefund_zeroAmount_ReturnFalse() {
        // testing zero transaction amount is invalid
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, 0, VALID_ORGANISER_MSG);
        assertFalse(result, "Should return false when transaction amount is zero");
    }

    @Test
    public void testProcessRefund_negativeAmount_ReturnFalse() {
        // testing negative transaction amount is invalid
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, -20.0, VALID_ORGANISER_MSG);
        assertFalse(result, "Should return false when transaction amount is negative");
    }

    @Test
    public void testProcessRefund_smallPositiveAmount_ReturnTrue() {
        // testing boundary: very small positive amount is valid
        boolean result = mockPaymentSystem.processRefund(VALID_NUM_TICKETS, VALID_EVENT_TITLE,
                VALID_STUDENT_EMAIL, VALID_STUDENT_PHONE, VALID_EP_EMAIL, 0.01, VALID_ORGANISER_MSG);
        assertTrue(result, "Should return true when transaction amount is a small positive value");
    }
}
