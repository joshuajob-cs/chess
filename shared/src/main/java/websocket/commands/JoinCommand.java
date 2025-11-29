package websocket.commands;

import chess.ChessGame;

public class JoinCommand extends UserGameCommand{
    ChessGame.TeamColor color;

    public JoinCommand(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor color){
        super(commandType, authToken, gameID);
        assert (commandType == CommandType.CONNECT);
        this.color = color;
    }

    public ChessGame.TeamColor color(){
        return color;
    }

    @Override
    public String toString() {
        return super.toString() +
                " {color=" + color +
                '}';
    }
}
