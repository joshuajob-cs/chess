package server;

import com.google.gson.Gson;
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
        // Register your endpoints and exception handlers here.

    }

    private void clear(Context ctx){
        clearService.clearAll();
        ctx.result("{}");
    }

    private void register(Context ctx){
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), UserData.class);
        LoginResponse response;
        try {
            response =  userService.register(request);
        } catch (dataaccess.DataAccessException ex) {
            ctx.status(Integer.parseInt(ex.getMessage()));
            ctx.result(serializer.toJson(Map.of("message", ex.getCause().getMessage())));
            return;
        }
        ctx.result(serializer.toJson(response));
    }

    public void login(Context ctx){
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), LoginData.class);
        LoginResponse response;
        try {
            response =  userService.login(request);
        } catch (dataaccess.DataAccessException ex) {
            ctx.status(Integer.parseInt(ex.getMessage()));
            ctx.result(serializer.toJson(Map.of("message", ex.getCause().getMessage())));
            return;
        }
        ctx.result(serializer.toJson(response));
    }

    public void logout(Context ctx){
        var serializer = new Gson();
        String request = ctx.header("authorization");
        try {
            userService.logout(request);
        } catch (dataaccess.DataAccessException ex) {
            ctx.status(Integer.parseInt(ex.getMessage()));
            ctx.result(serializer.toJson(Map.of("message", ex.getCause().getMessage())));
            return;
        }
        ctx.result("{}");
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {server.stop();}
}
