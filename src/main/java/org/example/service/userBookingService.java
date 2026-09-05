package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Train;
import org.example.entity.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class userBookingService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_PATH = "localDb/users.json";

    private List<User> userList;
    private User user;

    // Constructor for loading all users
    public userBookingService() throws IOException {
        loadUserListFromFile();
    }

    // Constructor used after login
    public userBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    // Load users from JSON file
    private void loadUserListFromFile() throws IOException {

        File usersFile = new File(USER_PATH);

        if (!usersFile.exists()) {

            System.out.println(
                    "users.json not found. Creating new file."
            );

            userList = new ArrayList<>();

            usersFile.getParentFile().mkdirs();

            objectMapper.writeValue(usersFile, userList);

            return;
        }

        userList = objectMapper.readValue(
                usersFile,
                new TypeReference<List<User>>() {}
        );

        if (userList == null) {
            userList = new ArrayList<>();
        }
    }

    // =========================
    // LOGIN
    // =========================
    public Boolean loginUser() {

        if (user == null) {
            return false;
        }

        Optional<User> foundUser = userList.stream()
                .filter(user1 ->
                        user1.getName().equalsIgnoreCase(user.getName())
                                && UserServiceUtil.checkPassword(
                                user.getPassword(),
                                user1.getHashedPassword()
                        )
                )
                .findFirst();

        return foundUser.isPresent();
    }

    // =========================
    // SIGN UP
    // =========================
    public Boolean signUp(User user1) {

        if (user1 == null) {
            return false;
        }

        try {

            boolean userExists = userList.stream()
                    .anyMatch(existingUser ->
                            existingUser.getName()
                                    .equalsIgnoreCase(user1.getName())
                    );

            if (userExists) {

                System.out.println("User already exists.");

                return false;
            }

            // Hash password
            String hashedPassword =
                    UserServiceUtil.hashPassword(
                            user1.getPassword()
                    );

            user1.setHashedPassword(hashedPassword);

            // Don't store plain password
            user1.setPassword(null);

            userList.add(user1);

            saveUserListToFile();

            System.out.println("Signup successful.");

            return true;

        } catch (IOException ex) {

            ex.printStackTrace();

            return false;
        }
    }

    // =========================
    // SAVE USERS
    // =========================
    private void saveUserListToFile() throws IOException {

        File usersFile = new File(USER_PATH);

        objectMapper.writeValue(
                usersFile,
                userList
        );
    }

    // =========================
    // FETCH BOOKINGS
    // =========================
    public void fetchBookings() {

        if (user == null) {
            System.out.println("Please login first.");
            return;
        }

        Optional<User> userFetched = userList.stream()
                .filter(user1 ->
                        user1.getName().equalsIgnoreCase(user.getName())
                                && UserServiceUtil.checkPassword(
                                user.getPassword(),
                                user1.getHashedPassword()
                        )
                )
                .findFirst();

        if (userFetched.isPresent()) {

            User loggedInUser = userFetched.get();

            if (loggedInUser.getTicketsBooked() == null
                    || loggedInUser.getTicketsBooked().isEmpty()) {

                System.out.println("No tickets booked.");

            } else {

                loggedInUser.printTickets();
            }

        } else {

            System.out.println("User not found.");
        }
    }

    // =========================
    // CANCEL BOOKING
    // =========================
    public Boolean cancelBooking(String ticketId) {

        if (ticketId == null || ticketId.isEmpty()) {

            System.out.println(
                    "Ticket ID cannot be null or empty."
            );

            return false;
        }

        if (user == null) {

            System.out.println("Please login first.");

            return false;
        }

        if (user.getTicketsBooked() == null
                || user.getTicketsBooked().isEmpty()) {

            System.out.println("No tickets booked.");

            return false;
        }

        boolean removed = user.getTicketsBooked()
                .removeIf(ticket ->
                        ticket.getTicketID().equals(ticketId)
                );

        if (removed) {

            try {

                // Find the actual user in userList
                Optional<User> loggedInUser =
                        userList.stream()
                                .filter(user1 ->
                                        user1.getName()
                                                .equalsIgnoreCase(
                                                        user.getName()
                                                )
                                )
                                .findFirst();

                if (loggedInUser.isPresent()) {

                    loggedInUser.get()
                            .setTicketsBooked(
                                    user.getTicketsBooked()
                            );

                    saveUserListToFile();
                }

                System.out.println(
                        "Ticket with ID " +
                                ticketId +
                                " has been canceled."
                );

                return true;

            } catch (IOException ex) {

                ex.printStackTrace();

                return false;
            }

        } else {

            System.out.println(
                    "No ticket found with ID " + ticketId
            );

            return false;
        }
    }

    // =========================
    // GET TRAINS
    // =========================
    public List<Train> getTrains(
            String source,
            String destination
    ) {

        try {

            TrainService trainService =
                    new TrainService();

            return trainService.searchTrains(
                    source,
                    destination
            );

        } catch (IOException ex) {

            ex.printStackTrace();

            return new ArrayList<>();
        }
    }

    // =========================
    // FETCH SEATS
    // =========================
    public List<List<Integer>> fetchSeats(Train train) {

        if (train == null ||
                train.getSeats() == null) {

            return new ArrayList<>();
        }

        return train.getSeats();
    }

    // =========================
    // BOOK TRAIN SEAT
    // =========================
    public Boolean bookTrainSeat(
            Train train,
            int row,
            int seat
    ) {

        try {

            if (user == null) {

                System.out.println(
                        "Please login before booking."
                );

                return false;
            }

            if (train == null ||
                    train.getSeats() == null) {

                return false;
            }

            TrainService trainService =
                    new TrainService();

            List<List<Integer>> seats =
                    train.getSeats();

            // Check row
            if (row < 0 ||
                    row >= seats.size()) {

                System.out.println(
                        "Invalid row."
                );

                return false;
            }

            // Check column
            if (seat < 0 ||
                    seat >= seats.get(row).size()) {

                System.out.println(
                        "Invalid seat."
                );

                return false;
            }

            // Check whether seat is already booked
            if (seats.get(row).get(seat) != 0) {

                System.out.println(
                        "Seat is already booked."
                );

                return false;
            }

            // Book seat
            seats.get(row).set(seat, 1);

            train.setSeats(seats);

            // Save updated train
            trainService.addTrain(train);

            System.out.println(
                    "Seat booked successfully."
            );

            return true;

        } catch (IOException ex) {

            ex.printStackTrace();

            return false;
        }
    }
}