package dataaccess;

import model.GameData;
import model.GameList;

import java.sql.SQLException;

public class SQLGameDAO implements GameDAO{

    @Override
    public void clear() {
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement("TRUNCATE TABLE game;")){
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createGame(GameData data) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public GameList listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException {

    }
}
