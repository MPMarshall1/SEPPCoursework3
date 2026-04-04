package com.example;

import java.util.ArrayList;
import java.util.Collection;

public class Student extends User{

    private String name;
    private int phoneNumber;

    private StudentPreferences preferences;

    private Collection<Booking> myBookings;

    public Student(String email, String password, String name, int phoneNumber) {

        // pass parameter to user class
        super(email, password);

        this.name = name;
        this.phoneNumber = phoneNumber;

        this.preferences = new StudentPreferences();
        this.myBookings = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        this.myBookings.add(booking);
    }

    public String getName() {
        return name;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public StudentPreferences getPreferences() {
        return this.preferences;
    }

    public Collection<Booking> getMyBookings() {
        return this.myBookings;
    }
}
