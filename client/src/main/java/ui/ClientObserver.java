package ui;

import chess.ChessGame;
import websocket.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

class ClientObserver implements ServerMessageObserver {
    ChessGame.TeamColor color = null;

    public void setColor(ChessGame.TeamColor color){
        this.color = color;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message) {
            case GameMessage game -> load(game);
            case ErrorMessage error-> error(error);
            case NotificationMessage note -> message(note);
            default -> throw new IllegalStateException("Unexpected value: " + message);
        }
    }

    private void load(GameMessage message){
        BoardUI.printBoard(message.game(), color);
    }

    private void error(ErrorMessage message){
        System.out.println(message.error());
    }

    private void message(NotificationMessage message){
        System.out.println(message.notification());
    }
}
