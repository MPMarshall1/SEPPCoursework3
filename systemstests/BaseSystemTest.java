package systemstests;

import org.junit.jupiter.api.BeforeEach;


import yourpackage.UserController;
import yourpackage.BookingController;
import yourpackage.EventPerformanceController;
import yourpackage.PaymentSystem;

import yourpackage.Student;
import yourpackage.AdminStaff;
import yourpackage.EntertainmentProvider;

import yourpackage.Event;
import yourpackage.Performance;
import yourpackage.EventType;

public abstract class BaseSystemTest {

    protected UserController userController;
    protected BookingController bookingController;
    protected EventPerformanceController eventPerformanceController;
    protected PaymentSystem paymentSystem;

    protected Student student;
    protected AdminStaff admin;
    protected EntertainmentProvider provider;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        paymentSystem = new PaymentSystem();
        bookingController = new BookingController();
        eventPerformanceController = new EventPerformanceController();

        student = new Student("student@gmail.com", "pw123", "Student One");
        admin = new AdminStaff("admin@gmail.com", "adminpw", "Admin One");
        provider = new EntertainmentProvider("ep@org.com", "eppw", "EP Org", "BN123");

        userController.addUser(student);
        userController.addUser(admin);
        userController.addUser(provider);
    }

    protected void loginAsStudent() {
        userController.login("student@gmail.com", "pw123");
    }

    protected void loginAsAdmin() {
        userController.login("admin@gmail.com", "adminpw");
    }

    protected void loginAsProvider() {
        userController.login("ep@org.com", "eppw");
    }

    protected void createEventWithPerformanceOwnedByProvider() {
        Event event = new Event(
                100L,
                "Spring Concert",
                EventType.Music,
                provider
        );

        Performance performance = new Performance(
                200L,
                "Spring Concert - Evening",
                event,
                20.0,
                100,
                true
        );

        eventPerformanceController.addEvent(event);
        eventPerformanceController.addPerformance(performance);
    }

    protected Performance getOnlyPerformance() {
        return eventPerformanceController.getPerformanceByID(200L);
    }

    protected Event getOnlyEvent() {
        return eventPerformanceController.getEventByID(100L);
    }

    protected Performance createUnticketedPerformanceOwnedByProvider() {
        Event event = new Event(
                101L,
                "Open Mic",
                EventType.Music,
                provider
        );

        Performance performance = new Performance(
                201L,
                "Open Mic Night",
                event,
                0.0,
                50,
                false
        );

        eventPerformanceController.addEvent(event);
        eventPerformanceController.addPerformance(performance);

        return performance;
    }

    protected EntertainmentProvider createAndRegisterOtherProvider() {
        EntertainmentProvider otherProvider =
                new EntertainmentProvider("other@org.com", "pw", "Other Org", "BN555");
        userController.addUser(otherProvider);
        return otherProvider;
    }
}