package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.JoinCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint{
    Session session;
    private final String url;
    private ServerMessageObserver observer;

    public WebSocketFacade(int port){
        try {
            url = "ws://localhost:" + port + "/ws";
            URI socketURI = new URI(url);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException("Error: You forgot to turn the server on. " + ex.getMessage());
        }
    }

    public WebSocketFacade(int port, ServerMessageObserver observer){
        this(port);
        this.observer = observer;
    }

    public void join(String authToken, int gameID, ChessGame.TeamColor color){
        try {
        var command = new JoinCommand(CONNECT, authToken, gameID, color);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not join because you forgot to turn the server on.");
        }
    }

    public void leave(String authToken, int gameID){
        try {
            var command = new UserGameCommand(LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not leave because you forgot to turn the server on.");
        }
    }

    public void move(String authToken, int gameID, ChessMove move){
        try {
            var command = new MoveCommand(MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not move the piece because you forgot to turn the server on.");
        }
    }

    public void resign(String authToken, int gameID){
        try {
            var command = new UserGameCommand(RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not resign because you forgot to turn the server on.");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(String.class, message -> {
            if (observer != null) {
                ServerMessage clientMessage = new Gson().fromJson(message, ServerMessage.class);
                switch (clientMessage.getServerMessageType()) {
                    case LOAD_GAME -> observer.notify(new Gson().fromJson(message, GameMessage.class));
                    case ERROR -> observer.notify(new Gson().fromJson(message, ErrorMessage.class));
                    case NOTIFICATION -> observer.notify(new Gson().fromJson(message, NotificationMessage.class));
                }
            }
        });
    }
}
