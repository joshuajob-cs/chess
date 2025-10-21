package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    public static Map<Integer, GameData> allGameData = new HashMap<>();

    @Override
    public void clear() {
        allGameData.clear();
    }

    @Override
    public void createGame(GameData data) throws DataAccessException {
        if (allGameData.containsKey(data.gameID())){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        allGameData.put(data.gameID(), data);
    }

    @Override
    public GameData getGame(int gameID) {
        return allGameData.get(gameID);
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(allGameData.values());
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException{
        if (!allGameData.containsKey(data.gameID())){
            throw new DataAccessException("400", new DataAccessException("Error: Bad Request"));
        }
        allGameData.put(data.gameID(), data);
    }
}
