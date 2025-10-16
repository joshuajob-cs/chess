package dataaccess;

import model.GameData;

public interface GameDAO {
    void clear();
    void createGame(GameData data) throws DataAccessException;
    GameData getGame(String gameID);
}
