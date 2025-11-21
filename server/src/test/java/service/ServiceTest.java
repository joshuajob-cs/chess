package service;

import chess.ChessGame;
import dataaccess.*;
import model.CreateGameRequest;
import model.JoinGameRequest;
import model.LoginData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    @Test
    void clear() throws DataAccessException {
        var clearer = new ClearService();
        clearer.clearAll();
        UserDAO userMemory = new SQLUserDAO();
        var userService = new UserService();
        GameDAO gameMemory = new SQLGameDAO();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("joe", "12345", "j@j"));
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "championship"));
        clearer.clearAll();
        assertNull(userMemory.getUser(credentials.username()));
        assertThrows(DataAccessException.class, () -> gameService.listGames(credentials.authToken()));
        assertNull(gameMemory.getGame(gameID.num()));
    }

    @Test
    void register() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        var userMemory = new SQLUserDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("reg", "12345", "j@j"));
        assertNotNull(userMemory.getUser(credentials.username()));
    }

    @Test
    void registerNeg() throws DataAccessException{
        var data = new UserData("sameReg", "12345", "j@j");
        var clearer = new ClearService();
        clearer.clearAll();
        var userService = new UserService();
        userService.register(data);
        assertThrows(DataAccessException.class, () -> userService.register(data));
    }

    @Test
    void login() throws DataAccessException{
        String username = "log";
        String password = "12345";
        var clearer = new ClearService();
        clearer.clearAll();
        AuthDAO authMemory = new SQLAuthDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData(username, password, "j@j"));
        credentials = userService.login(new LoginData(username, password));
        assertNotNull(authMemory.getAuth(credentials.authToken()));
    }

    @Test
    void loginNeg() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        var userService = new UserService();
        userService.register(new UserData("realLog", "12345", "j@j"));
        assertThrows(DataAccessException.class, () -> userService.login(new LoginData("fakeLog", "12345")));
    }

    @Test
    void logout() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        AuthDAO authMemory = new SQLAuthDAO();
        var userService = new UserService();
        var credentials = userService.register(new UserData("out", "12345", "j@j"));
        userService.logout(credentials.authToken());
        assertNull(authMemory.getAuth(credentials.authToken()));
    }

    @Test
    void logoutNeg() throws DataAccessException {
        var clearer = new ClearService();
        clearer.clearAll();
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
        var clearer = new ClearService();
        clearer.clearAll();
        var gameMemory = new SQLGameDAO();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("master", "12345", "j@j"));
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "fun-times"));
        assertNotNull(gameMemory.getGame(gameID.num()));
    }

    @Test
    void createGameNeg() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("number_one", "12345", "j@j"));
        assertThrows(DataAccessException.class, () -> gameService.createGame(new CreateGameRequest(credentials.authToken(), null)));
    }

    @Test
    void joinGame() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        var gameMemory = new SQLGameDAO();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("winner", "12345", "j@j"));
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "fun-times"));
        gameService.joinGame(new JoinGameRequest(credentials.authToken(), ChessGame.TeamColor.WHITE, gameID.num()));
        assertEquals("winner", gameMemory.getGame(gameID.num()).whiteUsername());
    }

    @Test
    void joinGameNeg() throws DataAccessException{
        var clearer = new ClearService();
        clearer.clearAll();
        var gameMemory = new SQLGameDAO();
        var userService = new UserService();
        var gameService = new GameService();
        var credentials = userService.register(new UserData("king", "12345", "j@j"));
        var gameID = gameService.createGame(new CreateGameRequest(credentials.authToken(), "fun-times"));
        gameService.joinGame(new JoinGameRequest(credentials.authToken(), ChessGame.TeamColor.BLACK, gameID.num()));
        assertNotEquals("king", gameMemory.getGame(gameID.num()).whiteUsername());
    }
}
