package websocket;

import chess.ChessBoard;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import server.DataAccessException;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameService game = new GameService();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx){
        try {
            try {
                System.out.println(ctx.message());
                UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
                switch (command.type()) {
                    case CONNECT -> join(command, ctx.session);
                    case MAKE_MOVE -> exit("me", ctx.session);
                    case LEAVE -> exit("me", ctx.session);
                    case RESIGN -> exit("me", ctx.session);
                }
            } catch (JsonParseException notCom) {
                ServerMessage message = new Gson().fromJson(ctx.message(), ServerMessage.class);
                switch (message.getServerMessageType()) {
                    case LOAD_GAME -> load();
                    case ERROR -> load();
                    case NOTIFICATION -> throw new IllegalStateException("Unexpected value: " + ctx);
                }

            } catch (DataAccessException ex) {
                var error = new ErrorMessage(ERROR, ex.getCause().getMessage());
                ctx.session.getRemote().sendString(new Gson().toJson(error));
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void join(UserGameCommand command, Session session) throws IOException, DataAccessException {
        validate(command.auth(), command.num());
        connections.add(session);
        String message = command.auth() + " entered the game!";
        var notification = new NotificationMessage(NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(new GameMessage(LOAD_GAME, new ChessBoard())));
        connections.broadcast(session, notification);
        System.out.println("broadcasted");

    }

    private void validate(String authToken, int gameNum) throws DataAccessException {
        game.validateRequest(authToken);
        if (gameNum < 1 || gameNum > game.listGames(authToken).size()) {
            throw new DataAccessException("400", new DataAccessException("Error: There is no game with that game number."));
        }
    }

    void move(){

    }

    void leave(){

    }

    void resign(){

    }

    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    private void load(){
        System.out.println("He's Loaded");
    }

    void error(){

    }

    private void message(){

    }

    public void makeNoise(String petName, String sound) {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            System.out.println("WHAT" + ex);
        }
    }
}