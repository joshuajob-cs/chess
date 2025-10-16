package dataaccess;

import model.GameData;

import java.util.HashMap;
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
}
