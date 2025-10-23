package service;

import dataaccess.*;
import model.CreateGameRequest;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.GameService;
import service.UserService;

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
}
