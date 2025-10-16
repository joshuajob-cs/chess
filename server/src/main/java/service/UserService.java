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
        if(data.username() == null || data.password() == null || data.email() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Bad Request"));
        }
        userMemoryAccess.createUser(data);
        var authToken = generateToken();
        authMemoryAccess.createAuth(new AuthData(authToken, data.username()));
        return new LoginResponse(data.username(), authToken);
    }

    public LoginResponse login(LoginData loginData) throws DataAccessException {
        if(loginData.username() == null || loginData.password() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Bad Request"));
        }
        var userData = userMemoryAccess.getUser(loginData.username());
        if (userData == null || !loginData.password().equals(userData.password())){
            throw new DataAccessException("401", new DataAccessException("Error: unauthorized"));
        }
        var authToken = generateToken();
        authMemoryAccess.createAuth(new AuthData(authToken, loginData.username()));
        return new LoginResponse(loginData.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException{
        authMemoryAccess.deleteAuth(authToken);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
