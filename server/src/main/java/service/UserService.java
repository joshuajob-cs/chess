package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.RegisterResponse;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userMemory = new MemoryUserDAO();

    public RegisterResponse register(UserData data) throws DataAccessException {
        userMemory.createUser(data);
        var authToken = generateToken();
        return new RegisterResponse(data.username(), authToken);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
