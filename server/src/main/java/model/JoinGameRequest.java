package model;

import chess.ChessGame;

public record JoinGameRequest(String authToken, ChessGame.TeamColor color, int gameID) {
}
