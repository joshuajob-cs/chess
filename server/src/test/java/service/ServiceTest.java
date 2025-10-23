package service;

import dataaccess.*;
import model.CreateGameRequest;
import model.LoginData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.eclipse.jetty.util.LazyList.isEmpty;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    @Test
    void clear() throws DataAccessException {
        UserDAO userMemory = new MemoryUserDAO();
        AuthDAO authMemory = new MemoryAuthDAO();
        GameDAO gameMemory = new MemoryGameDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("joe", "12345", "j@j"));
        assertNotNull(userMemory.getUser(credentials.username()));
        assertNotNull(authMemory.getAuth(credentials.authToken()));
        var gameService = new GameService();
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "championship"));
        assertNotNull(gameMemory.getGame(gameID));
        var clearer = new ClearService();
        clearer.clearAll();
        assertNull(userMemory.getUser(credentials.username()));
        assertThrows(DataAccessException.class, () -> gameService.listGames(credentials.authToken()));
        assertNull(gameMemory.getGame(gameID));
    }

    @Test
    void clearNeg() throws DataAccessException {
        UserDAO userMemory = new MemoryUserDAO();
        AuthDAO authMemory = new MemoryAuthDAO();
        GameDAO gameMemory = new MemoryGameDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("", "", ""));
        assertNotNull(userMemory.getUser(credentials.username()));
        assertNotNull(authMemory.getAuth(credentials.authToken()));
        var gameService = new GameService();
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "championship"));
        assertNotNull(gameMemory.getGame(gameID));
        assertFalse(isEmpty(gameMemory.listGames()));
    }

    @Test
    void register() throws DataAccessException{
        var userMemory = new MemoryUserDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("reg", "12345", "j@j"));
        assertNotNull(userMemory.getUser(credentials.username()));
    }

    @Test
    void registerNeg() throws DataAccessException{
        var userMemory = new MemoryUserDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("sameReg", "12345", "j@j"));
        assertNotNull(userMemory.getUser(credentials.username()));
        assertThrows(DataAccessException.class, () -> userService.register(new UserData("sameReg", "12345", "j@j")));
    }

    @Test
    void login() throws DataAccessException{
        var userMemory = new MemoryUserDAO();
        AuthDAO authMemory = new MemoryAuthDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("log", "12345", "j@j"));
        assertNotNull(userMemory.getUser(credentials.username()));
        credentials = userService.login(new LoginData("log", "12345"));
        assertNotNull(authMemory.getAuth(credentials.authToken()));
    }

    @Test
    void loginNeg() throws DataAccessException{
        var userMemory = new MemoryUserDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("realLog", "12345", "j@j"));
        assertNotNull(userMemory.getUser(credentials.username()));
        assertThrows(DataAccessException.class, () -> userService.login(new LoginData("fakeLog", "12345")));
    }

    @Test
    void logout() throws DataAccessException{

    }

    @Test
    void listGames() throws DataAccessException{

    }

    @Test
    void createGame() throws DataAccessException{

    }

    @Test
    void joinGame() throws DataAccessException{

    }
}
