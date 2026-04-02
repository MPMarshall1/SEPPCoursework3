package com.example;

import java.util.ArrayList;
import java.util.Collection;

public class EntertainmentProvider extends User{

    private String orgName;
    private String businessNumber;
    private String name;
    private String description;

    private Collection<Event> myEvents;

    public EntertainmentProvider(String email, String password, String orgName, String businessNumber, String name, String description) {

        super(email, password);

        this.orgName = orgName;
        this.businessNumber = businessNumber;
        this.name = name;
        this.description = description;

        this.myEvents = new ArrayList<>();
    }

    public void addEvent(Event event) {

        this.myEvents.add(event);
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

    public Collection<Event> getMyEvents() {
        return this.myEvents;
    }
}
