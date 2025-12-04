package ui;

import chess.ChessGame;
import websocket.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.GameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

class ClientObserver implements ServerMessageObserver {
    ChessGame.TeamColor color = null;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    enum Status{
        INITIAL,
        SUCCESS,
        ERROR
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setStatus(Status newStatus) {
        Status oldStatus = Status.INITIAL;
        support.firePropertyChange("status", oldStatus, newStatus);
    }

    public void setColor(ChessGame.TeamColor color){
        this.color = color;
    }

    public ChessGame.TeamColor getColor(){
        return color;
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
        setStatus(Status.SUCCESS);
    }

    private void error(ErrorMessage message){
        System.out.println(message.error());
        setStatus(Status.ERROR);
    }

    private void message(NotificationMessage message){
        System.out.println(message.notification());
        setStatus(Status.SUCCESS);
    }
}
