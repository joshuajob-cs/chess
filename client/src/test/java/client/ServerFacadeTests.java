package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.DataAccessException;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    public static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void clear() throws DataAccessException{
        String username = "a";
        String password = "b";
        facade.clear();
        try{
            facade.register(username, password, "c");
            facade.createGame("yay");
        } catch(DataAccessException e){
            Assertions.fail();
        }
        facade.clear();
        assertThrows(DataAccessException.class, () -> facade.login(username, password));
        assertThrows(DataAccessException.class, () -> facade.listGames());
    }

    @Test
    public void register() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        facade.register(username, password, "c");
        facade.logout();
        assertDoesNotThrow(() -> facade.login(username, password));
    }

    @Test
    public void registerFail() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        String email = "c";
        facade.register(username, password, email);
        assertThrows(DataAccessException.class, () -> facade.register(username, password, email));
    }

    @Test
    public void login() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        facade.register(username, password, "c");
        facade.logout();
        facade.login(username, password);
        assertDoesNotThrow(() -> facade.listGames());
    }

    @Test
    public void loginFail() throws DataAccessException{
        facade.clear();
        facade.register("a", "b", "c");
        facade.logout();
        assertThrows(DataAccessException.class, () -> facade.login("bad", "b"));
        assertThrows(DataAccessException.class, () -> facade.login("a", "bad"));
    }

    @Test
    public void logout() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        facade.register(username, password, "c");
        facade.logout();
        assertThrows(DataAccessException.class, () -> facade.listGames());
    }

    @Test
    public void logoutFail() throws DataAccessException{
        facade.clear();
        assertThrows(DataAccessException.class, () -> facade.logout());
    }

    @Test
    public void listGames() throws DataAccessException{
        facade.clear();
        facade.register("a", "b", "c");
        facade.createGame("yes");
        facade.createGame("no");
        facade.createGame("maybe");
        var games = facade.listGames();
        //System.out.println(games);
        assertEquals(3, games.games().size());

    }

    @Test
    public void listGamesFail() throws DataAccessException{
        facade.clear();
        assertThrows(DataAccessException.class, () -> facade.listGames());
    }

    @Test
    public void createGame() throws DataAccessException{
        String name = "yes";
        facade.clear();
        facade.register("a", "b", "c");
        facade.createGame(name);
        var games = new ArrayList<>(facade.listGames().games());
        int gameID = games.getFirst().gameID();
        assertEquals(new GameData(gameID, null, null, name, new ChessGame()), games.getFirst());
    }

    @Test
    public void createGameFail() throws DataAccessException{
        facade.clear();
        facade.register("a", "b", "c");
        facade.createGame("cool");
        assertThrows(DataAccessException.class, () -> facade.createGame(null));
    }

    @Test
    public void joinGame() throws DataAccessException{
        String name = "cool";
        String user = "me";
        facade.clear();
        facade.register(user, "b", "c");
        facade.createGame(name);
        facade.joinGame(ChessGame.TeamColor.WHITE, 1);
        var games = facade.listGames();
        assertEquals(new GameData(1, user, null, name, new ChessGame()), games.games().iterator().next());
    }

    @Test
    public void joinGameFail() throws DataAccessException{
        facade.clear();
        facade.register("me", "b", "c");
        int gameID = facade.createGame("cool");
        facade.joinGame(ChessGame.TeamColor.WHITE, gameID);
        assertThrows(DataAccessException.class, () -> facade.joinGame(ChessGame.TeamColor.WHITE, gameID));
    }

    @Test
    public void getGame() throws DataAccessException{
        facade.clear();
        facade.register("me", "b", "c");
        facade.createGame("boring");
        facade.createGame("cool");
        var game = facade.getGame(2);
        assertEquals(new ChessGame().getBoard(), game);
    }

    @Test
    public void getGameFail() throws DataAccessException {
        facade.clear();
        facade.register("me", "b", "c");
        facade.createGame("boring");
        assertThrows(DataAccessException.class, () -> facade.getGame(2));
    }
}
