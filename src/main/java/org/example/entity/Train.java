package org.example.entity;

import java.sql.Time;
import java.util.List;
import java.util.Map;

public class Train {

    private String trainId;

    private String trainNo;

    private List<List<Integer>> seats;

    private Map<String, Time> stationTime;

    private List<String> stations;

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public List<List<Integer>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<Integer>> seats) {
        this.seats = seats;
    }

    public Map<String, Time> getStationTime() {
        return stationTime;
    }

    public void setStationTime(Map<String, Time> stationTime) {
        this.stationTime = stationTime;
    }

    public List<String> getStations() {
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }

    public Train(String trainId, String trainNo, List<List<Integer>> seats, Map<String, Time> stationTime, List<String> stations) {
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
        this.stationTime = stationTime;
        this.stations = stations;
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainId='" + trainId + '\'' +
                ", trainNo='" + trainNo + '\'' +
                ", seats=" + seats +
                ", stationTime=" + stationTime +
                ", stations=" + stations +
                '}';
    }
}

