package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;

import io.javalin.http.Context;
import model.LoginData;
import model.LoginResponse;
import model.UserData;
import service.ClearService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService = new UserService();
    private final ClearService clearService = new ClearService();

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        // Register your endpoints and exception handlers here.

    }

    private void clear(Context ctx){
        clearService.clearAll();
        ctx.result("{}");
    }

    private void register(Context ctx){
        class RegisterRequest extends ServiceRequest{
            void requestService(){}

            String getResponse() throws DataAccessException{
                var serializer = new Gson();
                var request = serializer.fromJson(ctx.body(), UserData.class);
                LoginResponse response = userService.register(request);
                return serializer.toJson(response);
            }
        }

        RegisterRequest request = new RegisterRequest();
        request.sendServiceRequest(ctx);
    }

    private void login(Context ctx){
        class LoginRequest extends ServiceRequest{
            void requestService(){}

            String getResponse() throws DataAccessException{
                var serializer = new Gson();
                var request = serializer.fromJson(ctx.body(), LoginData.class);
                LoginResponse response = userService.login(request);
                return serializer.toJson(response);
            }
        }

        LoginRequest request = new LoginRequest();
        request.sendServiceRequest(ctx);
    }

    private void logout(Context ctx){
        class LogoutRequest extends ServiceRequest{
            void requestService() throws DataAccessException{
                String request = ctx.header("authorization");
                userService.logout(request);
            }

            String getResponse(){
                return "{}";
            }
        }

        LogoutRequest request = new LogoutRequest();
        request.sendServiceRequest(ctx);
    }

    private void listGames(Context ctx){

    }

    private void createGame(Context ctx){

    }

    private void joinGame(Context ctx){

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {server.stop();}

    public static abstract class ServiceRequest{
        void sendServiceRequest(Context ctx){
            var serializer = new Gson();
            String response;
            try {
                requestService();
                response = getResponse();
            } catch (dataaccess.DataAccessException ex) {
                ctx.status(Integer.parseInt(ex.getMessage()));
                ctx.result(serializer.toJson(Map.of("message", ex.getCause().getMessage())));
                return;
            }
            ctx.result(response);
        }

        abstract void requestService() throws DataAccessException;
        abstract String getResponse() throws DataAccessException;
    }
}
