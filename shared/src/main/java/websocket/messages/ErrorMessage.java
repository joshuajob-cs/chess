package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;

    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        assert (type == ServerMessageType.ERROR);
        this.errorMessage = message;
    }
}
