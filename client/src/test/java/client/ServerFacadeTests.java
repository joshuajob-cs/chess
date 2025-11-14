package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

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
    public void clear(){
        String username = "a";
        String password = "b";
        facade.clear();
        try{
            facade.register(new UserData(username, password, "c"));
            facade.createGame(new GameName("yay"));
        } catch(DataAccessException e){
            Assertions.fail();
        }
        facade.clear();
        assertThrows(DataAccessException.class, () -> facade.login(new LoginData(username, password)));
        assertThrows(DataAccessException.class, () -> facade.listGames());
    }

    @Test
    public void register() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        facade.register(new UserData(username, password, "c"));
        facade.logout();
        assertDoesNotThrow(() -> facade.login(new LoginData(username, password)));
    }

    @Test
    public void registerFail() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        String email = "c";
        facade.register(new UserData(username, password, email));
        assertThrows(DataAccessException.class, () -> facade.register(new UserData(username, password, email)));
    }

    @Test
    public void login() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        facade.register(new UserData(username, password, "c"));
        facade.logout();
        facade.login(new LoginData(username, password));
        assertDoesNotThrow(() -> facade.listGames());
    }

    @Test
    public void loginFail() throws DataAccessException{
        facade.clear();
        facade.register(new UserData("a", "b", "c"));
        facade.logout();
        assertThrows(DataAccessException.class, () -> facade.login(new LoginData("bad", "b")));
        assertThrows(DataAccessException.class, () -> facade.login(new LoginData("a", "bad")));
    }

    @Test
    public void logout() throws DataAccessException{
        facade.clear();
        String username = "a";
        String password = "b";
        facade.register(new UserData(username, password, "c"));
        facade.logout();
        assertThrows(DataAccessException.class, () -> facade.listGames());
    }

    @Test
    public void logoutFail() {
        facade.clear();
        assertThrows(DataAccessException.class, () -> facade.logout());
    }

    @Test
    public void listGames() throws DataAccessException{
        facade.clear();
        facade.register(new UserData("a", "b", "c"));
        facade.createGame(new GameName("yes"));
        facade.createGame(new GameName("no"));
        facade.createGame(new GameName("maybe"));
        var games = facade.listGames();
        //System.out.println(games);
        assertEquals(3, games.games().size());

    }

    @Test
    public void listGamesFail() {
        facade.clear();
        assertThrows(DataAccessException.class, () -> facade.listGames());
    }

    @Test
    public void createGame() throws DataAccessException{
        String name = "yes";
        facade.clear();
        facade.register(new UserData("a", "b", "c"));
        int gameID = facade.createGame(new GameName(name));
        var games = facade.listGames();

        assertEquals(new GameData(gameID, null, null, name, new ChessGame()), games.games().iterator().next());
    }

    @Test
    public void createGameFail() throws DataAccessException{
        facade.clear();
        facade.register(new UserData("a", "b", "c"));
        facade.createGame(new GameName("cool"));
        assertThrows(DataAccessException.class, () -> facade.createGame(new GameName(null)));
    }

    @Test
    public void joinGame() throws DataAccessException{
        String name = "cool";
        String user = "me";
        facade.clear();
        facade.register(new UserData(user, "b", "c"));
        facade.createGame(new GameName(name));
        facade.joinGame(new ColorAndId(ChessGame.TeamColor.WHITE, 1));
        var games = facade.listGames();
        assertEquals(new GameData(1, user, null, name, new ChessGame()), games.games().iterator().next());
    }

    @Test
    public void joinGameFail() throws DataAccessException{
        facade.clear();
        facade.register(new UserData("me", "b", "c"));
        int gameID = facade.createGame(new GameName("cool"));
        facade.joinGame(new ColorAndId(ChessGame.TeamColor.WHITE, gameID));
        assertThrows(DataAccessException.class, () -> facade.joinGame(new ColorAndId(ChessGame.TeamColor.WHITE, gameID)));
    }
}
