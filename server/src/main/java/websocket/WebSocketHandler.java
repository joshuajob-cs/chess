package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            System.out.println(ctx.message());
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.commandType()) {
                case CONNECT -> join("you", ctx.session);
                case LEAVE -> exit(action.authToken(), ctx.session);
            }
        } catch (JsonParseException notCom) {
            ServerMessage message = new Gson().fromJson(ctx.message(), ServerMessage.class);
            switch (message.getServerMessageType()) {
                case LOAD_GAME -> load();
                default -> throw new IllegalStateException("Unexpected value: " + ctx);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void join(String visitorName, Session session) throws IOException {
        connections.add(session);
        var message = String.format("Entered the game", visitorName);
        var notification = new ServerMessage(LOAD_GAME);
        connections.broadcast(null, notification);
        System.out.println("broadcasted");

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