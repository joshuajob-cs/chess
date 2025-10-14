package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;

import io.javalin.http.Context;
import model.RegisterResponse;
import model.UserData;

import java.util.Map;
import java.util.UUID;

public class Server {

    private final Javalin server;
    private final UserDAO userMemory = new MemoryUserDAO();

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx){
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), UserData.class);
        try {
            userMemory.createUser(request);
        } catch (dataaccess.DataAccessException ex) {
            ctx.status(403);
            ctx.result(serializer.toJson(Map.of("message", ex.getMessage())));
            return;
        }
        var authToken = generateToken();
        var response = new RegisterResponse(request.username(), authToken);
        ctx.result(serializer.toJson(response));
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {server.stop();}
}
