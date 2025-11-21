package websocket;

import chess.ChessBoard;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import server.DataAccessException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final SQLGameDAO game = new SQLGameDAO();
    private final SQLAuthDAO auth = new SQLAuthDAO();

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
                    case MAKE_MOVE -> leave(ctx.session);
                    case LEAVE -> leave(ctx.session);
                    case RESIGN -> leave(ctx.session);
                }
            } catch (JsonParseException notCom) {
                ServerMessage message = new Gson().fromJson(ctx.message(), ServerMessage.class);
                switch (message.getServerMessageType()) {
                    case LOAD_GAME -> load();
                    case ERROR -> load();
                    case NOTIFICATION -> throw new IllegalStateException("Unexpected value: " + ctx);
                }

            } catch (DataAccessException ex) {
                var error = new ErrorMessage(ERROR, ex.getMessage());
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
        validate(command.num());
        connections.add(session);
        var authData = auth.getAuth(command.auth());
        String username;
        if (authData == null){
            throw new DataAccessException("Error: You are not logged in.");
        } else{
            username = auth.getAuth(command.auth()).username();
        }
        String message = username + " entered the game!";
        var notification = new NotificationMessage(NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(new GameMessage(LOAD_GAME, new ChessBoard())));
        connections.broadcast(session, notification);
        System.out.println("broadcasted");

    }

    private void validate(int gameNum) throws DataAccessException {
        if (gameNum < 1 || gameNum > game.listGames().size()) {
            throw new DataAccessException("Error: There is no game with that game number.");
        }
    }

    private void move(){

    }

    private void leave(Session session){
        connections.remove(session);
    }

    private void resign(){

    }

    private void load(){
        System.out.println("He's Loaded");
    }

    private void error(){

    }

    private void message(){

    }
}