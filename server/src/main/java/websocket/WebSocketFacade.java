package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint{
    Session session;
    private final String url;

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

    public void join(String authToken, int gameNum){
        try {
        var command = new UserGameCommand(CONNECT, authToken, gameNum);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not join because you forgot to turn the server on.");
        }
    }

    public void leave(){
        try {
            var command = new UserGameCommand(LEAVE, "", 0);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not leave because you forgot to turn the server on.");
        }
        //Sends command; server redirects command to Websocket Handler
    }

    public void move(String authToken, int gameID, ChessMove move){
        try {
            var command = new MoveCommand(MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not move the piece because you forgot to turn the server on.");
            //Sends command; server redirects command to Websocket Handler
        }
    }

    public void resign(){
        try {
            var command = new UserGameCommand(RESIGN, "", 0);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Error: Can not resign because you forgot to turn the server on.");
        }
        //Sends command; server redirects command to Websocket Handler
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
