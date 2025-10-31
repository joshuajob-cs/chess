package service;

import chess.ChessGame;
import dataaccess.*;
import model.CreateGameRequest;
import model.JoinGameRequest;
import model.LoginData;
import model.UserData;
import org.junit.jupiter.api.Test;

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
        var userMemory = new MemoryUserDAO();
        AuthDAO authMemory = new MemoryAuthDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("out", "12345", "j@j"));
        assertNotNull(userMemory.getUser(credentials.username()));
        credentials = userService.login(new LoginData("out", "12345"));
        assertNotNull(authMemory.getAuth(credentials.authToken()));
        userService.logout(credentials.authToken());
        assertNull(authMemory.getAuth(credentials.authToken()));
    }

    @Test
    void logoutNeg() throws DataAccessException {
        var userService = new UserService();
        assertThrows(DataAccessException.class, () -> userService.logout("Non-token"));
    }

    @Test
    void listGames() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("gamer", "12345", "j@j"));
        gameService.createGame(new CreateGameRequest(credentials.authToken(), "semi-finals"));
        var listOfGames = gameService.listGames(credentials.authToken());
        assertEquals(1, listOfGames.games().size());
    }

    @Test
    void listGamesNeg() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("gamer", "12345", "j@j"));
        var listOfGames = gameService.listGames(credentials.authToken());
        assertNotEquals(1, listOfGames.games().size());
    }

    @Test
    void createGame() throws DataAccessException{
        var gameMemory = new MemoryGameDAO();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("master", "12345", "j@j"));
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "fun-times"));
        assertNotNull(gameMemory.getGame(gameID));
    }

    @Test
    void createGameNeg() throws DataAccessException{
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("number_one", "12345", "j@j"));
        assertThrows(DataAccessException.class, () -> gameService.createGame(new CreateGameRequest(credentials.authToken(), null)));
    }

    @Test
    void joinGame() throws DataAccessException{
        var gameMemory = new MemoryGameDAO();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("winner", "12345", "j@j"));
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "fun-times"));
        assertNotNull(gameMemory.getGame(gameID));
        gameService.joinGame(new JoinGameRequest(credentials.authToken(), ChessGame.TeamColor.WHITE, gameID));
        assertEquals("winner", gameMemory.getGame(gameID).whiteUsername());
    }

    @Test
    void joinGameNeg() throws DataAccessException{
        var gameMemory = new MemoryGameDAO();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("king", "12345", "j@j"));
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "fun-times"));
        assertNotNull(gameMemory.getGame(gameID));
        gameService.joinGame(new JoinGameRequest(credentials.authToken(), ChessGame.TeamColor.BLACK, gameID));
        assertNotEquals("king", gameMemory.getGame(gameID).whiteUsername());
    }
}
