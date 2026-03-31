package com.example;

public class Student extends User{

    private String name;
    private int phoneNumber;

    private StudentPreferences preferences;

    public Student(String email, String password, String name, int phoneNumber) {

        // pass parameter to user class
        super(email, password);

        this.name = name;
        this.phoneNumber = phoneNumber;
        this.preferences = new StudentPreferences();
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

    // TODO: implement addBooking method
}
