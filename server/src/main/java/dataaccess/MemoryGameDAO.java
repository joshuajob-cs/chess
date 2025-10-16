package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    public static Map<String, GameData> allGameData = new HashMap<>();

    @Override
    public void clear() {
        allGameData.clear();
    }

    @Override
    public void createGame(GameData data) throws DataAccessException {

    }

    @Override
    public GameData getGame(String gameID) {
        return null;
    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException{

    }
}
