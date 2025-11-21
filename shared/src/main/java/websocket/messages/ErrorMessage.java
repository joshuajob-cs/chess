package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;

    public ErrorMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        this.errorMessage = message;
    }
}
