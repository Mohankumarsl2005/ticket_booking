package org.example;

import org.example.entity.Train;
import org.example.entity.User;
import org.example.service.userBookingService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static userBookingService bookingService;

    private static Train selectedTrain;

    public static void main(String[] args) {

        System.out.println("Running Train Booking System");

        try {

            bookingService = new userBookingService();

            boolean running = true;

            while (running) {

                printMenu();

                String option = scanner.nextLine().trim();

                switch (option) {

                    case "1":
                        signUp();
                        break;

                    case "2":
                        login();
                        break;

                    case "3":
                        fetchBookings();
                        break;

                    case "4":
                        searchTrains();
                        break;

                    case "5":
                        bookSeat();
                        break;

                    case "6":
                        cancelBooking();
                        break;

                    case "7":
                        running = false;
                        System.out.println(
                                "Thank you for using Train Booking System!"
                        );
                        break;

                    default:
                        System.out.println(
                                "Invalid option. Please try again."
                        );
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Unable to start application: " + e.getMessage()
            );
        }

        scanner.close();
    }

    // ========================= MENU =========================

    private static void printMenu() {

        System.out.println();
        System.out.println("Choose option");
        System.out.println("1. Sign up");
        System.out.println("2. Login");
        System.out.println("3. Fetch Bookings");
        System.out.println("4. Search Trains");
        System.out.println("5. Book a Seat");
        System.out.println("6. Cancel my Booking");
        System.out.println("7. Exit the App");
        System.out.println();
    }

    // ========================= SIGN UP =========================

    private static void signUp() {

        System.out.println("Enter the username to signup");

        String username = scanner.nextLine().trim();

        System.out.println("Enter the password to signup");

        String password = scanner.nextLine();

        if (username.isEmpty()) {

            System.out.println("Username cannot be empty.");
            return;
        }

        if (password.trim().isEmpty()) {

            System.out.println("Password cannot be empty.");
            return;
        }

        User newUser = new User(username, password);

        boolean success = bookingService.signUp(newUser);

        if (!success) {

            System.out.println("Signup failed.");
        }
    }

    // ========================= LOGIN =========================

    private static void login() {

        System.out.println("Enter the username to Login");

        String username = scanner.nextLine().trim();

        System.out.println("Enter the password to Login");

        String password = scanner.nextLine();

        if (username.isEmpty()) {

            System.out.println("Username cannot be empty.");
            return;
        }

        if (password.trim().isEmpty()) {

            System.out.println("Password cannot be empty.");
            return;
        }

        User loginUser = new User(username, password);

        try {

            userBookingService loginService =
                    new userBookingService(loginUser);

            boolean success = loginService.loginUser();

            if (success) {

                bookingService = loginService;

                System.out.println("You are now logged in.");

            } else {

                System.out.println("Login failed.");
            }

        } catch (IOException e) {

            System.out.println(
                    "Login error: " + e.getMessage()
            );
        }
    }

    // ========================= FETCH BOOKINGS =========================

    private static void fetchBookings() {

        System.out.println("Fetching your bookings...");

        bookingService.fetchBookings();
    }

    // ========================= SEARCH TRAINS =========================

    private static void searchTrains() {

        System.out.println("Type your source station");

        String source = scanner.nextLine().trim();

        System.out.println("Type your destination station");

        String destination = scanner.nextLine().trim();

        if (source.isEmpty()) {

            System.out.println("Source cannot be empty.");
            return;
        }

        if (destination.isEmpty()) {

            System.out.println("Destination cannot be empty.");
            return;
        }

        List<Train> trains =
                bookingService.getTrains(source, destination);

        if (trains == null || trains.isEmpty()) {

            System.out.println("No trains found.");

            selectedTrain = null;

            return;
        }

        System.out.println();
        System.out.println("Available trains:");

        for (int i = 0; i < trains.size(); i++) {

            System.out.println(
                    (i + 1) + ". " + trains.get(i)
            );
        }

        System.out.println();
        System.out.println("Select train number:");

        try {

            int choice =
                    Integer.parseInt(scanner.nextLine().trim());

            if (choice < 1 || choice > trains.size()) {

                System.out.println("Invalid train selection.");
                return;
            }

            selectedTrain = trains.get(choice - 1);

            System.out.println(
                    "Train selected successfully."
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid number."
            );
        }
    }

    // ========================= BOOK SEAT =========================

    private static void bookSeat() {

        if (selectedTrain == null) {

            System.out.println(
                    "Please search and select a train first."
            );

            return;
        }

        List<List<Integer>> seats =
                bookingService.fetchSeats(selectedTrain);

        if (seats == null || seats.isEmpty()) {

            System.out.println("No seats available.");

            return;
        }

        System.out.println();
        System.out.println("Seat Layout");
        System.out.println("0 = Available");
        System.out.println("1 = Booked");
        System.out.println();

        for (int i = 0; i < seats.size(); i++) {

            System.out.println(
                    "Row " + i + ": " + seats.get(i)
            );
        }

        try {

            System.out.println("Enter row number:");

            int row =
                    Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Enter seat number:");

            int seat =
                    Integer.parseInt(scanner.nextLine().trim());

            bookingService.bookTrainSeat(
                    selectedTrain,
                    row,
                    seat
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter valid numbers."
            );
        }
    }

    // ========================= CANCEL BOOKING =========================

    private static void cancelBooking() {

        System.out.println(
                "Enter the ticket ID to cancel:"
        );

        String ticketId =
                scanner.nextLine().trim();

        if (ticketId.isEmpty()) {

            System.out.println(
                    "Ticket ID cannot be empty."
            );

            return;
        }

        bookingService.cancelBooking(ticketId);
    }
}