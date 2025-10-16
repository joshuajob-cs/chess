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
        if(authMemoryAccess.isLoggedIn(loginData.username())){
            throw new DataAccessException("403", new DataAccessException("Error: already logged in"));
        }
        var userData = userMemoryAccess.getUser(loginData.username());
        if (userData == null){
            throw new DataAccessException("401", new DataAccessException("Error: unauthorized"));
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
