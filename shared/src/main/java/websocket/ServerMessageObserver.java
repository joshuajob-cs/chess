package websocket;

import websocket.messages.ServerMessage;

public class ServerMessageObserver{
    private static int cheese = 0;

    public static void setCheese() {
        ServerMessageObserver.cheese = cheese + 1;
    }

    public static int getCheese() {
        return cheese;
    }
}
