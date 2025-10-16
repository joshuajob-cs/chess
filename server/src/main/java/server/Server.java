package server;

import com.google.gson.Gson;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;

import io.javalin.http.Context;
import model.RegisterResponse;
import model.UserData;
import service.UserService;

import java.util.Map;
import java.util.UUID;

public class Server {

    private final Javalin server;
    private final UserService userService = new UserService();

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx){
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), UserData.class);
        RegisterResponse response;
        try {
            response =  userService.register(request);
        } catch (dataaccess.DataAccessException ex) {
            ctx.status(Integer.parseInt(ex.getMessage()));
            ctx.result(serializer.toJson(Map.of("message", ex.getCause().getMessage())));
            return;
        }
        ctx.result(serializer.toJson(response));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {server.stop();}
}
