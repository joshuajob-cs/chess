package ui;
import server.ServerFacade;
import websocket.WebSocketFacade;

public class Router {
    private final ServerFacade hi = new ServerFacade(6);
    private final WebSocketFacade hi2 = new WebSocketFacade(6);
}