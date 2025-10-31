package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTest {
    @Test
    void clearUser() throws DataAccessException {
        var user = new UserData("joe", "12345", "j@j");
        var userMemory = new SQLUserDAO();
        userMemory.clear();
        assertNull(userMemory.getUser(user.username()));
        userMemory.createUser(user);
        assertNotNull(userMemory.getUser(user.username()));
        userMemory.clear();
        assertNull(userMemory.getUser(user.username()));
    }

    @Test
    void createUser() throws DataAccessException{
        var user = new UserData("bob", "12345", "j@j");
        var userMemory = new SQLUserDAO();
        userMemory.clear();
        userMemory.createUser(user);
        assertNotNull(userMemory.getUser(user.username()));
    }

    @Test
    void createUserNeg() throws DataAccessException{
        var user = new UserData("bob", "12345", "j@j");
        var userMemory = new SQLUserDAO();
        userMemory.clear();
        userMemory.createUser(user);
        assertThrows(DataAccessException.class, () -> userMemory.createUser(user));
    }

    @Test
    void getUser() throws DataAccessException{
        var user = new UserData("joe", "12345", "j@j");
        var userMemory = new SQLUserDAO();
        userMemory.clear();
        userMemory.createUser(user);
        assertEquals(user, userMemory.getUser(user.username()));
    }

    @Test
    void getUserNeg() throws DataAccessException{
        var userMemory = new SQLUserDAO();
        userMemory.clear();
        assertNull(userMemory.getUser("bob"));
    }

    @Test
    void clearAuth() throws DataAccessException{
        var auth = new AuthData("nonsense", "joe");
        var authMemory = new SQLAuthDAO();
        authMemory.clear();
        assertNull(authMemory.getAuth(auth.authToken()));
        authMemory.createAuth(auth);
        assertNotNull(authMemory.getAuth(auth.authToken()));
        authMemory.clear();
        assertNull(authMemory.getAuth(auth.authToken()));
    }

    @Test
    void createAuth() throws DataAccessException{
        var auth = new AuthData("nonsense", "joe");
        AuthDAO authMemory = new SQLAuthDAO();
        authMemory.clear();
        authMemory.createAuth(auth);
        assertNotNull(authMemory.getAuth(auth.authToken()));
    }

    @Test
    void createAuthNeg() throws DataAccessException{
        var auth = new AuthData("nonsense", "joe");
        AuthDAO authMemory = new SQLAuthDAO();
        authMemory.clear();
        authMemory.createAuth(auth);
        assertNotEquals(new AuthData("hackerToken", "joe"), authMemory.getAuth(auth.authToken()));
    }

    @Test
    void getAuth() throws DataAccessException{
        var auth = new AuthData("nonsense", "bob");
        AuthDAO authMemory = new SQLAuthDAO();
        authMemory.clear();
        authMemory.createAuth(auth);
        assertEquals(auth, authMemory.getAuth(auth.authToken()));
    }

    @Test
    void getAuthNeg() throws DataAccessException{
        AuthDAO authMemory = new SQLAuthDAO();
        authMemory.clear();
        assertNull(authMemory.getAuth("hackerToken"));
    }

    @Test
    void deleteAuth() throws DataAccessException{
        var auth = new AuthData("nonsense", "bob");
        AuthDAO authMemory = new SQLAuthDAO();
        authMemory.clear();
        authMemory.createAuth(auth);
        assertNotNull(authMemory.getAuth(auth.authToken()));
        authMemory.deleteAuth(auth.authToken());
        assertNull(authMemory.getAuth(auth.authToken()));
    }

    @Test
    void deleteAuthNeg() throws DataAccessException{
        AuthDAO authMemory = new SQLAuthDAO();
        authMemory.clear();
        assertThrows(DataAccessException.class, () -> authMemory.deleteAuth("Ghost User"));
    }

    @Test
    void clearGame() throws DataAccessException{
        var game = new GameData(1, "joe", "bob", "championship", new ChessGame());
        GameDAO gameMemory = new SQLGameDAO();
        gameMemory.clear();
        assertNull(gameMemory.getGame(game.gameID()));
        gameMemory.createGame(game);
        assertNotNull(gameMemory.getGame(game.gameID()));
        gameMemory.clear();
        assertNull(gameMemory.getGame(game.gameID()));
    }

    @Test
    void createGame() throws DataAccessException{
        var game = new GameData(1, "bob", "joe", "championship", new ChessGame());
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        gameMemory.createGame(game);
        assertNotNull(gameMemory.getGame(game.gameID()));
    }

    @Test
    void createGameNeg() throws DataAccessException{
        var game = new GameData(1, "bob", "joe", "championship", new ChessGame());
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        gameMemory.createGame(game);
        assertThrows(DataAccessException.class, () -> gameMemory.createGame(game));
    }

    @Test
    void getGame() throws DataAccessException{
        var game = new GameData(1, "bob", "joe", "championship", new ChessGame());
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        gameMemory.createGame(game);
        assertEquals(game, gameMemory.getGame(game.gameID()));
    }

    @Test
    void getGameNeg() throws DataAccessException{
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        assertNull(gameMemory.getGame(-1));
    }

    @Test
    void listGames() throws DataAccessException{
        var game = new GameData(1, "bob", "joe", "championship", new ChessGame());
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        gameMemory.createGame(game);
        var list = gameMemory.listGames();
        assertEquals(1, list.games().size());
        assertEquals(game, new ArrayList<>(list.games()).getFirst());
    }

    @Test
    void listGamesNeg() throws DataAccessException{
        var game = new GameData(1, "bob", "joe", "championship", new ChessGame());
        var game2 = new GameData(2, "julie", "sven", "semi-final", new ChessGame());
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        gameMemory.createGame(game);
        gameMemory.createGame(game2);
        var list = gameMemory.listGames();
        assertNotEquals(1, list.games().size());
        assertNotEquals(game2, new ArrayList<>(list.games()).getFirst());
        assertNotEquals(game, new ArrayList<>(list.games()).get(1));
    }

    @Test
    void updateGame() throws DataAccessException{
        var game = new GameData(1, "bob", "joe", "championship", new ChessGame());
        var game2 = new GameData(1, "adam", "serena", "championship", new ChessGame());
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        gameMemory.createGame(game);
        assertEquals(game, gameMemory.getGame(game.gameID()));
        gameMemory.updateGame(game2);
        assertEquals(game2, gameMemory.getGame(game.gameID()));
    }

    @Test
    void updateGameNeg() throws DataAccessException{
        var game = new GameData(1, "bob", "joe", "championship", new ChessGame());
        var gameMemory = new SQLGameDAO();
        gameMemory.clear();
        assertThrows(DataAccessException.class, () -> gameMemory.updateGame(game));
    }
}
