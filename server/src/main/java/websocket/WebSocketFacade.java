package websocket;

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
            throw new RuntimeException("EEK NO " + ex.getMessage());
        }
    }

    public void join(String authToken, int gameNum){
        try {
        var command = new UserGameCommand(CONNECT, authToken, gameNum);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(" NOOOOOOOO");
        }
    }

    public void leave(){
        try {
            var command = new UserGameCommand(LEAVE, "", 0);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(" NOOOOOOOO");
        }
        //Sends command; server redirects command to Websocket Handler
    }

    public void move(){
        try {
            var command = new MoveCommand(MAKE_MOVE, "", 0, null);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(" NOOOOOOOO");
            //Sends command; server redirects command to Websocket Handler
        }
    }

    public void resign(){
        try {
            var command = new UserGameCommand(RESIGN, "", 0);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException(" NOOOOOOOO");
        }
        //Sends command; server redirects command to Websocket Handler
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
