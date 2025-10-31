package dataaccess;

import model.GameData;
import model.GameList;


public interface GameDAO {
    void clear() throws DataAccessException;
    void createGame(GameData data) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    GameList listGames() throws DataAccessException;
    void updateGame(GameData data) throws DataAccessException;
}
