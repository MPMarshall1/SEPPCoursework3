package com.example;

public class EntertainmentProvider extends User{

    private String orgName;
    private String businessNumber;
    private String name;
    private String description;

    public EntertainmentProvider(String email, String password, String orgName, String businessNumber, String name, String description) {

        super(email, password);

        this.orgName = orgName;
        this.businessNumber = businessNumber;
        this.name = name;
        this.description = description;
    }

    public void addEvent(Event event) {
        // TODO
    }

    // getters
    public String getOrgName() {
        return orgName;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
