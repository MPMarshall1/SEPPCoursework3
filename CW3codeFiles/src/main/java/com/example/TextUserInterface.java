package com.example;

import java.util.Collection;
import java.util.Scanner;

public class TextUserInterface implements View{

    private Scanner sc;

    public TextUserInterface() {
        this.sc = new Scanner(System.in);
    }

    @Override
    public String getInput(String inputPrompt) {

        System.out.print(inputPrompt);
        return sc.nextLine().trim();
    }

    @Override
    public void displaySuccess(String successMessage) {
        System.out.println("[SUCCESS] "+successMessage);
    }

    @Override
    public void displayError(String errorMessage) {
        System.out.println("[ERROR] "+errorMessage);
    }

    @Override
    public void displayListofPerformances(Collection<String> listOfPerformanceInfo) {
        System.out.println("--- Performance List ---");

        for (String info : listOfPerformanceInfo) {
            System.out.println(info);
        }
        System.out.println("------------------------");
    }

    @Override
    public void displaySpecificPerformance(String performanceInfo) {

        System.out.println("--- Performance Details ---");
        System.out.println(performanceInfo);
        System.out.println("---------------------------");
    }

    @Override
    public void displayBookingRecord(String bookingRecord) {
        System.out.println("--- Booking Record ---");
        System.out.println(bookingRecord);
        System.out.println("----------------------");
    }
}
