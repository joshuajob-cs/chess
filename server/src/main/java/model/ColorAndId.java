package model;

import chess.ChessGame;

public record ColorAndId(ChessGame.TeamColor playerColor, int gameID) {
}
