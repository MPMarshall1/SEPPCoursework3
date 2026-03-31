package com.example;

import java.time.LocalDateTime;

public class Booking {

    Student student;
    Performance performance;
    private long bookingNumber;
    private int numTickets;
    private double amountPaid;
    private LocalDateTime bookingDateTime;
    private BookingStatus status;

    public Booking(Student student, Performance performance, long bookingNumber, int numTickets, double amountPaid, LocalDateTime bookingDateTime) {
        this.student = student;
        this.performance = performance;
        this.bookingNumber = bookingNumber;
        this.numTickets = numTickets;
        this.amountPaid = amountPaid;
        this.bookingDateTime = bookingDateTime;
        this.status = BookingStatus.ACTIVE;
    }

    public void cancelbyStudent() {
        this.status = BookingStatus.CANCELLEDBYSTUDENT;
    }

    public void cancelPaymentFailed() {
        this.status = BookingStatus.PAYMENTFAILED;
    }

    public void cancelByProvider() {
        this.status = BookingStatus.CANCELLEDBYPROVIDER;
    }

    public boolean checkBookedByStudent(String email) {
        return student.getEmail().equals(email);
    }

    public String getStudentDetails() {
        String name = "Student Name: "+student.getName();
        String email = ", Student Email: "+student.getEmail();
        String number = ", Student Number: "+student.getPhoneNumber();

        return name + email + number;
    }

    public String generateBookingRecord() {
        String number = "Booking Number: "+this.bookingNumber;
        String event = " | Event: "+performance.getEventTitle();
        String ticketNum = " | "+this.numTickets;
        String totalCost = " | "+this.amountPaid;

        return number + event + ticketNum + totalCost;
    }

    // getters
    public int getNumTickets() {
        return numTickets;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public BookingStatus getStatus() {
        return status;
    }
}
