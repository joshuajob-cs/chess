package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
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

import static chess.ChessPosition.unparse;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService user = new UserService();
    private final GameService game = new GameService();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx){
        try {
            try {
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
                catch (JsonParseException ex){}
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
    public void handleClose(WsCloseContext ctx) {}

    private void join(JoinCommand command, Session session) throws IOException, DataAccessException {
        String username = user.getName(command.auth());
        String message;
        if(command.color() != null) {
            game.joinGame(new JoinGameRequest(command.auth(), command.color(), command.num()));
            message = username + " entered the game as " + command.color() + "!";
        }
        else{
            game.getGame(new GetGameRequest(command.auth(), command.num()));
            message = username + " is watching you!";
        }
        connections.add(command.num(), session);
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
        GameData data = game.move(new MoveRequest(command.auth(), command.num(), command.move()));
        ChessGame chessGame = data.game();
        String username = user.getName(command.auth());
        String opponent;
        if (data.whiteUsername().equals(username)){
            opponent = data.blackUsername();
        }
        else{
            opponent = data.whiteUsername();
        }

        ChessGame.GameState state = chessGame.getState();
        if (state == ChessGame.GameState.CHECK){
            var notification = new NotificationMessage(NOTIFICATION, opponent + " is in check!");
            connections.broadcast(command.num(), null, notification);
        } else if(state == ChessGame.GameState.CHECKMATE){
            var notification = new NotificationMessage(NOTIFICATION, opponent + " is in checkmate!");
            connections.broadcast(command.num(), null, notification);
        } else if(state == ChessGame.GameState.STALEMATE){
            var notification = new NotificationMessage(NOTIFICATION, opponent + " is in stalemate!");
            connections.broadcast(command.num(), null, notification);
        }

        var gameMessage = new GameMessage(LOAD_GAME, chessGame.getBoard());
        connections.broadcast(command.num(), null, gameMessage);
        var notification = new NotificationMessage(NOTIFICATION, username + " moved from " +
                unparse(command.move().getStartPosition()) + " to " + unparse(command.move().getEndPosition()) + "!");
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