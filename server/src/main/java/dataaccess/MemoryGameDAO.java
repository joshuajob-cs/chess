package dataaccess;

import model.GameData;
import model.GameList;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    public static Map<Integer, GameData> data = new HashMap<>();

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void createGame(GameData newData) throws DataAccessException {
        if (data.containsKey(newData.gameID())){
            throw new DataAccessException("403", new DataAccessException("Error: The gameID you tried has already been taken"));
        }
        data.put(newData.gameID(), newData);
    }

    @Override
    public GameData getGame(int gameID) {
        return data.get(gameID);
    }

    @Override
    public GameList listGames() {
        return new GameList(data.values());
    }

    @Override
    public void updateGame(GameData newData) throws DataAccessException{
        if (!data.containsKey(newData.gameID())){
            throw new DataAccessException("400", new DataAccessException("Error: There are no games with the game number you tried"));
        }
        data.put(newData.gameID(), newData);
    }
}
