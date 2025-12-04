package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        var sessions = connections.get(gameID);
        if (sessions == null){
            sessions = new ArrayList<>();
        }
        sessions.add(session);
        connections.put(gameID, sessions);
    }

    public void remove(Integer gameID, Session session) {
        var sessions = connections.get(gameID);
        sessions.remove(session);
        connections.put(gameID, sessions);
    }

    public void broadcast(Integer gameID, Session excludeSession, ServerMessage notification) throws IOException {
        var sessions = connections.get(gameID);
        Gson gson = new Gson();
        String msg = gson.toJson(notification);
        for (Session c : sessions) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
