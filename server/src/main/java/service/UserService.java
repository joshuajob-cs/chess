package service;

import dataaccess.*;
import model.AuthData;
import model.LoginData;
import model.LoginResponse;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final UserDAO userMemory = new SQLUserDAO();
    private final AuthDAO authMemory = new SQLAuthDAO();

    public LoginResponse register(UserData data) throws DataAccessException {
        if(data.username() == null || data.password() == null || data.email() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Missing username, password, or email"));
        }
        data = new UserData(data.username(), hashPassword(data.password()), data.email());
        userMemory.createUser(data);
        var authToken = generateToken();
        authMemory.createAuth(new AuthData(authToken, data.username()));
        return new LoginResponse(data.username(), authToken);
    }

    public LoginResponse login(LoginData loginData) throws DataAccessException {
        if(loginData.username() == null || loginData.password() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Missing username or password"));
        }
        var userData = userMemory.getUser(loginData.username());
        if (userData == null || !BCrypt.checkpw(loginData.password(), userData.password())){
            throw new DataAccessException("401", new DataAccessException("Error: Incorrect username or password"));
        }
        var authToken = generateToken();
        authMemory.createAuth(new AuthData(authToken, loginData.username()));
        return new LoginResponse(loginData.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException{
        if(authToken == null){
            throw new DataAccessException("401", new DataAccessException("Error: Missing authToken"));
        }
        authMemory.deleteAuth(authToken);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private static String hashPassword(String unhashedPassword){
        return BCrypt.hashpw(unhashedPassword, BCrypt.gensalt());
    }
}
