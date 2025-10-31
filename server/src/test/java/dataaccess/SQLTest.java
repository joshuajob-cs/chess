package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTest {
    @Test
    void clearUser() throws DataAccessException {
        var user = new UserData("joe", "12345", "j@j");
        UserDAO userMemory = new SQLUserDAO();
        userMemory.clear();
        assertNull(userMemory.getUser(user.username()));
        userMemory.createUser(user);
        assertNotNull(userMemory.getUser(user.username()));
        userMemory.clear();
        assertNull(userMemory.getUser(user.username()));
    }

    @Test
    void getUser() throws DataAccessException{
        var user = new UserData("joe", "12345", "j@j");
        UserDAO userMemory = new SQLUserDAO();
        userMemory.clear();
        userMemory.createUser(user);
        assertEquals(userMemory.getUser(user.username()),user);
    }

    @Test
    void clearAuth() throws DataAccessException{
        var auth = new AuthData("nonsense", "joe");
        AuthDAO authMemory = new SQLAuthDAO();
        authMemory.clear();
        assertNull(authMemory.getAuth(auth.authToken()));
        authMemory.createAuth(auth);
        assertNotNull(authMemory.getAuth(auth.authToken()));
        authMemory.clear();
        assertNull(authMemory.getAuth(auth.authToken()));
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
}
