package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{
    Session session;
    private final String url;

    public WebSocketFacade(int port){
        try {
            url = "ws://localhost:" + port + "/ws";
            URI socketURI = new URI(url);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                }
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException("EEK NO " + ex.getMessage());
        }
    }

    public void join(int gameNum){
        try {
        var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, "", gameNum);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new RuntimeException(" NOOOOOOOO");
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
