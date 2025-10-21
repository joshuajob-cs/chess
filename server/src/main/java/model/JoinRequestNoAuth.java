package model;

import chess.ChessGame;

public record JoinRequestNoAuth(ChessGame.TeamColor playerColor, int gameID) {
}
