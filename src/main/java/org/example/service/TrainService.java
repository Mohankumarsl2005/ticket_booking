package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TRAIN_DB_PATH =
            "localDb/trains.json";

    private List<Train> trainList;

    public TrainService() throws IOException {
        loadTrainListFromFile();
    }

    // =========================
    // LOAD TRAINS
    // =========================

    private void loadTrainListFromFile() throws IOException {

        File trainFile = new File(TRAIN_DB_PATH);

        System.out.println(
                "Loading trains from: " +
                        trainFile.getAbsolutePath()
        );

        // Create folder if it doesn't exist
        if (trainFile.getParentFile() != null) {
            trainFile.getParentFile().mkdirs();
        }

        // If file doesn't exist
        if (!trainFile.exists()) {

            System.out.println(
                    "trains.json not found. Creating default train."
            );

            trainList = new ArrayList<>();

            createDefaultTrain();

            saveTrainList();

            return;
        }

        // Read file
        trainList = objectMapper.readValue(
                trainFile,
                new TypeReference<List<Train>>() {}
        );

        // If JSON contains null
        if (trainList == null) {
            trainList = new ArrayList<>();
        }

        // IMPORTANT:
        // If JSON is [] create a default train
        if (trainList.isEmpty()) {

            System.out.println(
                    "trains.json is empty. Creating default train."
            );

            createDefaultTrain();

            saveTrainList();
        }

        System.out.println(
                "Total trains loaded: " +
                        trainList.size()
        );

        for (Train train : trainList) {

            if (train == null) {
                continue;
            }

            System.out.println(
                    "Train: " +
                            train.getTrainNo() +
                            " | Stations: " +
                            train.getStations()
            );
        }
    }

    // =========================
    // DEFAULT TRAIN
    // =========================

    private void createDefaultTrain() {

        Train train = new Train();

        train.setTrainId("bacs");

        train.setTrainNo("12345");

        train.setSeats(
                Arrays.asList(
                        new ArrayList<>(Arrays.asList(
                                0, 0, 0, 0, 0, 0
                        )),
                        new ArrayList<>(Arrays.asList(
                                0, 0, 0, 0, 0, 0
                        )),
                        new ArrayList<>(Arrays.asList(
                                0, 0, 0, 0, 0, 0
                        )),
                        new ArrayList<>(Arrays.asList(
                                0, 0, 0, 0, 0, 0
                        ))
                )
        );

        train.setStations(
                Arrays.asList(
                        "bangalore",
                        "jaipur",
                        "delhi"
                )
        );

        train.setStationTimes(
                new java.util.HashMap<String, String>() {{
                    put("bangalore", "13:50:00");
                    put("jaipur", "16:50:00");
                    put("delhi", "20:50:00");
                }}
        );

        trainList.add(train);

        System.out.println(
                "Default train created: 12345"
        );
    }

    // =========================
    // SEARCH TRAINS
    // =========================

    public List<Train> searchTrains(
            String source,
            String destination) {

        List<Train> result =
                new ArrayList<>();

        if (source == null ||
                destination == null) {

            return result;
        }

        source =
                source.trim().toLowerCase();

        destination =
                destination.trim().toLowerCase();

        System.out.println(
                "Searching: " +
                        source +
                        " -> " +
                        destination
        );

        for (Train train : trainList) {

            if (train == null) {
                continue;
            }

            List<String> stations =
                    train.getStations();

            if (stations == null ||
                    stations.isEmpty()) {

                continue;
            }

            int sourceIndex = -1;

            int destinationIndex = -1;

            for (int i = 0;
                 i < stations.size();
                 i++) {

                String station =
                        stations.get(i);

                if (station == null) {
                    continue;
                }

                station =
                        station.trim().toLowerCase();

                if (station.equals(source)) {

                    sourceIndex = i;
                }

                if (station.equals(destination)) {

                    destinationIndex = i;
                }
            }

            System.out.println(
                    "Train " +
                            train.getTrainNo() +
                            " sourceIndex=" +
                            sourceIndex +
                            " destinationIndex=" +
                            destinationIndex
            );

            // Source must come before destination
            if (sourceIndex != -1 &&
                    destinationIndex != -1 &&
                    sourceIndex < destinationIndex) {

                train.setSource(source);

                train.setDestination(destination);

                result.add(train);
            }
        }

        return result;
    }

    // =========================
    // ADD / UPDATE TRAIN
    // =========================

    public void addTrain(
            Train train) throws IOException {

        if (train == null) {
            return;
        }

        boolean found = false;

        for (int i = 0;
             i < trainList.size();
             i++) {

            Train existing =
                    trainList.get(i);

            if (existing != null &&
                    existing.getTrainNo() != null &&
                    existing.getTrainNo()
                            .equals(train.getTrainNo())) {

                trainList.set(i, train);

                found = true;

                break;
            }
        }

        if (!found) {

            trainList.add(train);
        }

        saveTrainList();
    }

    // =========================
    // SAVE TRAINS
    // =========================

    private void saveTrainList()
            throws IOException {

        File trainFile =
                new File(TRAIN_DB_PATH);

        if (trainFile.getParentFile() != null) {

            trainFile.getParentFile().mkdirs();
        }

        objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(
                        trainFile,
                        trainList
                );

        System.out.println(
                "Train data saved successfully."
        );
    }

    // =========================
    // GET ALL TRAINS
    // =========================

    public List<Train> getAllTrains() {

        return new ArrayList<>(
                trainList
        );
    }
}