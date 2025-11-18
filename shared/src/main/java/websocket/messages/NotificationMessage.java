package websocket.messages;

import chess.ChessBoard;

public class NotificationMessage extends ServerMessage{
    String message;

    public NotificationMessage(ServerMessage.ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }
}
