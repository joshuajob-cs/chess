package dataaccess;

import model.GameData;
import model.GameList;


public interface GameDAO {
    void clear();
    void createGame(GameData data) throws DataAccessException;
    GameData getGame(int gameID);
    GameList listGames();
    void updateGame(GameData data) throws DataAccessException;
}
