package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class userBookingService {
    private User user;

    private List<User> uaerList;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USER_PATH="../localDb/users.json";


    public userBookingService(User user) throws IOException {
        this.user = user;
        File users = new File(USER_PATH);
        uaerList = objectMapper.readValue(users, new TypeReference<List<User>>() {
        });


    }
}
