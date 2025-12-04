package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GetGameRequest;
import model.JoinGameRequest;
import model.MoveRequest;
import org.eclipse.jetty.websocket.api.Session;
import server.DataAccessException;
import service.GameService;
import service.UserService;
import websocket.commands.JoinCommand;
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
                        case CONNECT -> join(new Gson().fromJson(ctx.message(), JoinCommand.class), ctx.session);
                        case MAKE_MOVE -> move(new Gson().fromJson(ctx.message(), MoveCommand.class), ctx.session);
                        case LEAVE -> leave(command, ctx.session);
                        case RESIGN -> resign(command, ctx.session);
                    }
                    return;
                }
                catch (JsonParseException _){}
                try{
                    new Gson().fromJson(ctx.message(), ServerMessage.class);
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

    private void join(JoinCommand command, Session session) throws IOException, DataAccessException {
        if(command.color() != null) {
            game.joinGame(new JoinGameRequest(command.auth(), command.color(), command.num()));
        }
        else{
            game.getGame(new GetGameRequest(command.auth(), command.num()));
        }
        connections.add(command.num(), session);
        String username = user.getName(command.auth());
        String message = username + " entered the game!";
        var notification = new NotificationMessage(NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(new GameMessage(LOAD_GAME, new ChessGame().getBoard())));
        connections.broadcast(command.num(), session, notification);

    }

    private void leave(UserGameCommand command, Session session) throws IOException, DataAccessException{
        game.leave(new GetGameRequest(command.auth(), command.num()));
        connections.remove(command.num(), session);
        String username = user.getName(command.auth());
        String message = username + " left the game!";
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.broadcast(command.num(), null, notification);
    }

    private void move(MoveCommand command, Session session) throws IOException, DataAccessException{
        ChessBoard board = game.move(new MoveRequest(command.auth(), command.num(), command.move()));
        var gameMessage = new GameMessage(LOAD_GAME, board);
        var notification = new NotificationMessage(NOTIFICATION, "A move was made");
        connections.broadcast(command.num(), null, gameMessage);
        connections.broadcast(command.num(), session, notification);
    }

    private void resign(UserGameCommand command, Session session) throws IOException, DataAccessException{
        game.resign(new GetGameRequest(command.auth(), command.num()));
        String username = user.getName(command.auth());
        String message = username + " resigned!";
        session.getRemote().sendString(new Gson().toJson(new NotificationMessage(NOTIFICATION, "You resigned!")));
        var notification = new NotificationMessage(NOTIFICATION, message);
        connections.broadcast(command.num(), session, notification);
    }
}