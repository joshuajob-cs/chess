package server;

import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import dataaccess.DataAccessException;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();
    private final ClearService clearService = new ClearService();

    public Server(){
        initialize();
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

    }

    private void clear(Context ctx){
        clearService.clearAll();
        ctx.result("{}");
    }

    private void register(Context ctx){
        class RegisterRequest extends ServiceRequest{

            String send() throws DataAccessException{
                var serializer = new Gson();
                var request = serializer.fromJson(ctx.body(), UserData.class);
                LoginResponse response = userService.register(request);
                return serializer.toJson(response);
            }
        }

        RegisterRequest requester = new RegisterRequest();
        requester.process(ctx);
    }

    private void login(Context ctx){
        class LoginRequest extends ServiceRequest{

            String send() throws DataAccessException{
                var serializer = new Gson();
                var request = serializer.fromJson(ctx.body(), LoginData.class);
                LoginResponse response = userService.login(request);
                return serializer.toJson(response);
            }
        }

        LoginRequest requester = new LoginRequest();
        requester.process(ctx);
    }

    private void logout(Context ctx){
        class LogoutRequest extends ServiceRequest{

            String send() throws DataAccessException{
                String request = ctx.header("authorization");
                userService.logout(request);
                return "{}";
            }
        }

        LogoutRequest requester = new LogoutRequest();
        requester.process(ctx);
    }

    private void listGames(Context ctx){
        class ListRequest extends ServiceRequest{

            String send() throws DataAccessException{
                var serializer = new Gson();
                String request = ctx.header("authorization");
                GameList response = gameService.listGames(request);
                return serializer.toJson(response);
            }
        }

        ListRequest requester = new ListRequest();
        requester.process(ctx);
    }

    private void createGame(Context ctx){
        class CreationRequest extends ServiceRequest{

            String send() throws DataAccessException{
                var serializer = new Gson();
                String auth = ctx.header("authorization");
                var gameName = serializer.fromJson(ctx.body(), GameName.class);
                var request = new CreateGameRequest(auth, gameName.str());
                int response = gameService.createGame(request);
                return serializer.toJson(Map.of("gameID",response));
            }
        }

        CreationRequest requester = new CreationRequest();
        requester.process(ctx);
    }

    private void joinGame(Context ctx){
        class JoinRequest extends ServiceRequest{

            String send() throws DataAccessException{
                var serializer = new Gson();
                String auth = ctx.header("authorization");
                var attributes = serializer.fromJson(ctx.body(), ColorAndId.class);
                var request = new JoinGameRequest(auth, attributes.playerColor(), attributes.gameID());
                gameService.joinGame(request);
                return "{}";
            }
        }

        JoinRequest requester = new JoinRequest();
        requester.process(ctx);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {server.stop();}

    public static void initialize(){
        try {
            DatabaseManager.createDatabase();
        } catch (dataaccess.DataAccessException ex) {
            System.out.println(ex.getCause().getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static abstract class ServiceRequest{
        void process(Context ctx){
            var serializer = new Gson();
            String response;
            try {
                response = send();
            } catch (dataaccess.DataAccessException ex) {
                ctx.status(Integer.parseInt(ex.getMessage()));
                ctx.result(serializer.toJson(Map.of("message", ex.getCause().getMessage())));
                return;
            }
            ctx.result(response);
        }

        abstract String send() throws DataAccessException;
    }
}
