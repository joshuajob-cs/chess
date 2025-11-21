package websocket.messages;

public class NotificationMessage extends ServerMessage{
    String message;

    public NotificationMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        assert (type == ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
