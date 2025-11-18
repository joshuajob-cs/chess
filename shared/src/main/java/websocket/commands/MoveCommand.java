package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand{
    ChessMove move;

    public MoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move){
        super(commandType, authToken, gameID);
        this.move = move;
    }
}
