package client;

import dataaccess.DataAccessException;
import model.GameName;
import model.LoginData;
import model.UserData;
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
    public void listGames() {
        assertTrue(true);
    }

    @Test
    public void listGamesFail() {
        assertTrue(true);
    }

    @Test
    public void createGame() {
        assertTrue(true);
    }

    @Test
    public void createGameFail() {
        assertTrue(true);
    }

    @Test
    public void joinGame() {
        assertTrue(true);
    }

    @Test
    public void joinGameFail() {
        assertTrue(true);
    }
}
