package service;

import dataaccess.*;
import model.AuthData;
import model.LoginData;
import model.LoginResponse;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userMemoryAccess = new MemoryUserDAO();
    private final AuthDAO authMemoryAccess = new MemoryAuthDAO();

    public LoginResponse register(UserData data) throws DataAccessException {
        userMemoryAccess.createUser(data);
        var authToken = generateToken();
        authMemoryAccess.createAuth(new AuthData(authToken, data.username()));
        return new LoginResponse(data.username(), authToken);
    }

    public LoginResponse login(LoginData loginData) throws DataAccessException {
        var userData = userMemoryAccess.getUser(loginData.username());
        if (userData == null){
            throw new DataAccessException("401", new DataAccessException("Error: unauthorized"));
        }
        var authData = authMemoryAccess.getAuthWithUsername(loginData.username());
        if(authData != null){
            return new LoginResponse(authData.username(), authData.authToken());
        }
        var authToken = generateToken();
        authMemoryAccess.createAuth(new AuthData(authToken, loginData.username()));
        return new LoginResponse(loginData.username(), authToken);
    }

    public void logout(){

    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
