package org.example;

import org.example.entity.Train;
import org.example.entity.User;
import org.example.service.userBookingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Running Train Booking System");

        Scanner scanner = new Scanner(System.in);

        int option = 0;

        userBookingService userBookingService;

        userBookingService = new userBookingService();

        Train trainSelectedForBooking = null;

        while (option != 7) {

            System.out.println("\nChoose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            option = scanner.nextInt();

            switch (option) {

                // =========================
                // SIGN UP
                // =========================
                case 1:

                    System.out.println("Enter the username to signup");
                    String nameToSignUp = scanner.next();

                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = scanner.next();

                    User userToSignup = new User(
                            nameToSignUp,
                            passwordToSignUp,
                            null,
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    boolean signedUp = userBookingService.signUp(userToSignup);

                    if (signedUp) {
                        System.out.println("Signup successful!");
                    } else {
                        System.out.println("Signup failed.");
                    }

                    break;


                // =========================
                // LOGIN
                // =========================
                case 2:

                    System.out.println("Enter the username to Login");
                    String nameToLogin = scanner.next();

                    System.out.println("Enter the password to Login");
                    String passwordToLogin = scanner.next();

                    User userToLogin = new User(
                            nameToLogin,
                            passwordToLogin,
                            null,
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );

                    userBookingService =
                            new userBookingService();

                    if (userBookingService.loginUser()) {
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Invalid username or password.");
                    }

                    break;


                // =========================
                // FETCH BOOKINGS
                // =========================
                case 3:

                    System.out.println("Fetching your bookings...");

                    userBookingService.fetchBookings();

                    break;


                // =========================
                // SEARCH TRAINS
                // =========================
                case 4:

                    System.out.println("Type your source station");
                    String source = scanner.next();

                    System.out.println("Type your destination station");
                    String destination = scanner.next();

                    List<Train> trains =
                            userBookingService.getTrains(
                                    source,
                                    destination
                            );

                    if (trains.isEmpty()) {
                        System.out.println("No trains found.");
                        break;
                    }

                    int index = 1;

                    for (Train train : trains) {

                        System.out.println(
                                index + ". Train ID: "
                                        + train.getTrainId()
                        );

                        System.out.println(
                                "Train No: "
                                        + train.getTrainNo()
                        );

                        for (Map.Entry<String, java.sql.Time> entry
                                : train.getStationTime().entrySet()) {

                            System.out.println(
                                    "Station "
                                            + entry.getKey()
                                            + " time: "
                                            + entry.getValue()
                            );
                        }

                        index++;
                    }

                    System.out.println(
                            "Select a train by typing 1, 2, 3..."
                    );

                    int trainChoice = scanner.nextInt();

                    if (trainChoice < 1 ||
                            trainChoice > trains.size()) {

                        System.out.println("Invalid train selection.");
                        break;
                    }

                    trainSelectedForBooking =
                            trains.get(trainChoice - 1);

                    System.out.println(
                            "Selected train: "
                                    + trainSelectedForBooking.getTrainId()
                    );

                    break;


                // =========================
                // BOOK SEAT
                // =========================
                case 5:

                    if (trainSelectedForBooking == null) {
                        System.out.println(
                                "Please search and select a train first."
                        );
                        break;
                    }

                    System.out.println(
                            "Select a seat out of these seats:"
                    );

                    List<List<Integer>> seats =
                            userBookingService.fetchSeats(
                                    trainSelectedForBooking
                            );

                    for (int i = 0; i < seats.size(); i++) {

                        System.out.print("Row " + i + ": ");

                        for (Integer value : seats.get(i)) {
                            System.out.print(value + " ");
                        }

                        System.out.println();
                    }

                    System.out.println("Enter the row:");
                    int row = scanner.nextInt();

                    System.out.println("Enter the column:");
                    int col = scanner.nextInt();

                    System.out.println("Booking your seat...");

                    Boolean booked =
                            userBookingService.bookTrainSeat(
                                    trainSelectedForBooking,
                                    row,
                                    col
                            );

                    if (Boolean.TRUE.equals(booked)) {
                        System.out.println(
                                "Booked! Enjoy your journey."
                        );
                    } else {
                        System.out.println(
                                "Can't book this seat."
                        );
                    }

                    break;


                // =========================
                // CANCEL BOOKING
                // =========================
                case 6:

                    System.out.println(
                            "Enter the ticket ID to cancel:"
                    );

                    String ticketId = scanner.next();

                    boolean cancelled =
                            userBookingService.cancelBooking(
                                    ticketId
                            );

                    if (cancelled) {
                        System.out.println(
                                "Booking cancelled successfully."
                        );
                    } else {
                        System.out.println(
                                "Unable to cancel booking."
                        );
                    }

                    break;


                // =========================
                // EXIT
                // =========================
                case 7:

                    System.out.println(
                            "Thank you for using Train Booking System!"
                    );

                    break;


                default:

                    System.out.println(
                            "Invalid option. Please choose 1-7."
                    );
            }
        }

        scanner.close();
    }
}