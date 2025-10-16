package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.RegisterResponse;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userMemoryAccess = new MemoryUserDAO();

    public RegisterResponse register(UserData data) throws DataAccessException {
        userMemoryAccess.createUser(data);
        var authToken = generateToken();
        return new RegisterResponse(data.username(), authToken);
    }

    //Log in
    //Log out

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
