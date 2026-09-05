package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Train;
import org.example.entity.User;
import org.example.entity.Ticket;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class userBookingService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_PATH = "localDb/users.json";

    private List<User> userList;
    private User user;

    // =========================
    // CONSTRUCTORS
    // =========================

    // Used for signup
    public userBookingService() throws IOException {
        loadUserListFromFile();
    }

    // Used after login
    public userBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    // =========================
    // LOAD USERS
    // =========================

    private void loadUserListFromFile() throws IOException {

        File usersFile = new File(USER_PATH);

        if (!usersFile.exists()) {

            System.out.println("users.json not found. Creating new file.");

            userList = new ArrayList<>();

            if (usersFile.getParentFile() != null) {
                usersFile.getParentFile().mkdirs();
            }

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

        if (user.getName() == null || user.getPassword() == null) {
            return false;
        }

        Optional<User> foundUser = userList.stream()
                .filter(storedUser ->
                        storedUser.getName() != null
                                && storedUser.getHashedPassword() != null
                                && storedUser.getName()
                                .equalsIgnoreCase(user.getName())
                                && UserServiceUtil.checkPassword(
                                user.getPassword(),
                                storedUser.getHashedPassword()
                        )
                )
                .findFirst();

        if (foundUser.isPresent()) {

            // Store the actual user from JSON
            this.user = foundUser.get();

            System.out.println("Login successful.");

            return true;
        }

        System.out.println("Invalid username or password.");

        return false;
    }

    // =========================
    // SIGN UP
    // =========================

    public Boolean signUp(User user) {

        if (user == null) {
            return false;
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {

            System.out.println("Username cannot be empty.");

            return false;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {

            System.out.println("Password cannot be empty.");

            return false;
        }

        try {

            String username = user.getName();

            // Check whether username already exists
            boolean userExists = userList.stream()
                    .anyMatch(existingUser ->
                            existingUser.getName() != null
                                    && existingUser.getName()
                                    .equalsIgnoreCase(username)
                    );

            if (userExists) {

                System.out.println("User already exists.");

                return false;
            }

            // Hash password
            String hashedPassword =
                    UserServiceUtil.hashPassword(
                            user.getPassword()
                    );

            user.setHashedPassword(hashedPassword);

            // Generate user ID
            if (user.getUserId() == null ||
                    user.getUserId().isEmpty()) {

                user.setUserId(
                        UUID.randomUUID().toString()
                );
            }

            // Initialize ticket list
            if (user.getTicketsBooked() == null) {

                user.setTicketsBooked(
                        new ArrayList<>()
                );
            }

            // Don't store plain password
            user.setPassword(null);

            // Add user
            userList.add(user);

            // Save to JSON
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

        if (usersFile.getParentFile() != null) {
            usersFile.getParentFile().mkdirs();
        }

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

        if (user.getTicketsBooked() == null ||
                user.getTicketsBooked().isEmpty()) {

            System.out.println("No tickets booked.");

            return;
        }

        user.printTickets();
    }

    // =========================
    // CANCEL BOOKING
    // =========================

    public Boolean cancelBooking(String ticketId) {

        if (ticketId == null || ticketId.trim().isEmpty()) {

            System.out.println(
                    "Ticket ID cannot be null or empty."
            );

            return false;
        }

        if (user == null) {

            System.out.println("Please login first.");

            return false;
        }

        if (user.getTicketsBooked() == null ||
                user.getTicketsBooked().isEmpty()) {

            System.out.println("No tickets booked.");

            return false;
        }

        boolean removed = user.getTicketsBooked()
                .removeIf(ticket ->
                        ticket != null
                                && ticket.getTicketID() != null
                                && ticket.getTicketID()
                                .equals(ticketId)
                );

        if (!removed) {

            System.out.println(
                    "No ticket found with ID " + ticketId
            );

            return false;
        }

        try {

            // Find the actual user using userId
            Optional<User> loggedInUser =
                    userList.stream()
                            .filter(storedUser ->
                                    storedUser.getUserId() != null
                                            && user.getUserId() != null
                                            && storedUser.getUserId()
                                            .equals(user.getUserId())
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

                System.out.println(
                        "Train or seats not available."
                );

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

            // Check seat
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