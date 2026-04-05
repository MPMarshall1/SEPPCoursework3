package com.example;

public class Main {
    public static void main(String[] args) {
        View textView = new TextUserInterface();
        MenuController menu = new MenuController(textView);
        menu.mainMenu();
    }
}