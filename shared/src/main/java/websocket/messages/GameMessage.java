package websocket.messages;

import chess.ChessBoard;

public class GameMessage extends ServerMessage{
    ChessBoard game;

    public GameMessage(ServerMessageType type, ChessBoard game) {
        super(type);
        assert (type == ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
