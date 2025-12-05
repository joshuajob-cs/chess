package model;

import chess.ChessMove;

public record MoveRequest(String authToken, int gameID, ChessMove move) {
    @Override
    public String toString() {
        return "MoveRequest{" +
                "authToken='" + authToken + '\'' +
                ", gameID=" + gameID +
                ", move=" + move +
                '}';
    }
}
