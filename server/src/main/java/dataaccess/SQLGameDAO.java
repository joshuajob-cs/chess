package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameList;

import java.util.ArrayList;

import static dataaccess.SQLUtilities.*;

public class SQLGameDAO implements GameDAO{

    @Override
    public void clear() {
        SQLClear("game");
    }

    @Override
    public void createGame(GameData data) throws DataAccessException {
        if(exists(String.valueOf(data.gameID()), "gameID", "game")){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        insert(data, "game");
    }

    @Override
    public GameData getGame(int gameID) {
        ArrayList<String> data = find(String.valueOf(gameID), "gameID", "game", 5);
        if (data == null){
            return null;
        }
        var serializer = new Gson();
        return new GameData(Integer.parseInt(data.get(0)), data.get(1), data.get(2), data.get(3), serializer.fromJson(data.get(4), ChessGame.class));
    }

    @Override
    public GameList listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException {

    }
}
