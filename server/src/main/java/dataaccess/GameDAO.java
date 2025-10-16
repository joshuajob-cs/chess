package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear();
    void createGame(GameData data) throws DataAccessException;
    GameData getGame(String gameID);
    List<GameData> listGames();
    void updateGame(GameData data) throws DataAccessException;
}
