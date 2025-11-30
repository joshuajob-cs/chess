package websocket;

import websocket.messages.ServerMessage;

public class ServerMessageObserver{
    private String cheese = "";

    public void setCheese(String cheese) {
        this.cheese = cheese;
    }

    public String getCheese() {
        return cheese;
    }
}
