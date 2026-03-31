package com.example;

public class AdminStaff extends User{

    private String name;

    public AdminStaff(String email, String password, String name) {
        super(email, password);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
