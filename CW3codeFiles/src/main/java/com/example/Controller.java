package com.example;

import java.util.Collection;

public abstract class Controller {

    protected User currentUser;
    protected View view = new TextUserInterface();

    public Controller() {
        this.currentUser = null;
    }

    protected boolean checkCurrentUserIsGuest() {

        return this.currentUser == null;
    }

    protected boolean checkCurrentUserIsEntertainmentProvider() {

        if (this.currentUser == null) {
            return false;
        }

        return this.currentUser instanceof EntertainmentProvider;
    }
    protected boolean checkCurrentUserIsStudent() {

        if (this.currentUser == null) {
            return false;
        }

        return this.currentUser instanceof Student;
    }

    protected boolean checkCurrentUserIsAdmin() {

        if (this.currentUser == null) {
            return false;
        }

        return this.currentUser instanceof AdminStaff;
    }

    protected <T> int selectFromMenu(Collection<T> list, String prompt) {
        // TODO
        return 0;
    }
}
