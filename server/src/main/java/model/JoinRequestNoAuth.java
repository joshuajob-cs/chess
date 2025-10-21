package model;

import chess.ChessGame;

public record JoinRequestNoAuth(ChessGame.TeamColor color, int gameID) {
}
