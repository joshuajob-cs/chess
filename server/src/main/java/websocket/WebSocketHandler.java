package websocket;

import chess.ChessBoard;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.SQLAuthDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GetGameRequest;
import org.eclipse.jetty.websocket.api.Session;
import server.DataAccessException;
import service.GameService;
import service.UserService;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService user = new UserService();
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
                try{
                    UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
                    switch (command.type()) {
                        case CONNECT -> join(command, ctx.session);
                        case MAKE_MOVE -> move(new Gson().fromJson(ctx.message(), MoveCommand.class), ctx.session);
                        case LEAVE -> leave(command, ctx.session);
                        case RESIGN -> resign(command, ctx.session);
                    }
                    return;
                }
                catch (JsonParseException _){}
                try{
                    ServerMessage message = new Gson().fromJson(ctx.message(), ServerMessage.class);
                    switch (message.getServerMessageType()) {
                        case LOAD_GAME -> load(new Gson().fromJson(ctx.message(), GameMessage.class));
                        case ERROR -> error(new Gson().fromJson(ctx.message(), ErrorMessage.class));
                        case NOTIFICATION -> message(new Gson().fromJson(ctx.message(), NotificationMessage.class));
                    }
                }
                catch (JsonParseException ex){
                    throw new RuntimeException("Error: Did not recognize message");
                }
            }
            catch (DataAccessException ex) {
                var error = new ErrorMessage(ERROR, ex.getCause().getMessage());
                ctx.session.getRemote().sendString(new Gson().toJson(error));
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void join(UserGameCommand command, Session session) throws IOException, DataAccessException {
        game.getGame(new GetGameRequest(command.auth(), command.num()));
        connections.add(session);
        String username = user.getName(command.auth());
        String message = username + " entered the game!";
        var notification = new NotificationMessage(NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(new GameMessage(LOAD_GAME, new ChessBoard())));
        connections.broadcast(session, notification);
        System.out.println("broadcasted");

    }

    private void leave(UserGameCommand command, Session session){
        connections.remove(session);
        //Calls GameService
        //Sends a notification to everyone
    }

    private void move(UserGameCommand command, Session session){
        game.move();
        //Calls GameService
        //Sends a load game message back to everyone
    }

    private void resign(UserGameCommand command, Session session){
        //Calls GameService
        //Sends a load game message back to everyone
    }

    private void load(GameMessage message){
        System.out.println("He's Loaded");
    }

    private void error(ErrorMessage message){

    }

    private void message(NotificationMessage message){

    }
}