package model;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) implements SQLData{
    @Override
    public List<String> toSQL() {
        var serializer = new Gson();
        return Arrays.asList(String.valueOf(gameID), whiteUsername, blackUsername, gameName, serializer.toJson(game));
    }
}
