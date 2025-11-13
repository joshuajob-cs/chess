package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameList;

import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.SQLUtilities.*;

public class SQLGameDAO implements GameDAO{

    @Override
    public void clear() throws DataAccessException{
        sqlClear("game");
    }

    @Override
    public void createGame(GameData data) throws DataAccessException {
        if(exists(String.valueOf(data.gameID()), "gameID", "game")){
            throw new DataAccessException("403", new DataAccessException("Error: The gameID you tried has already been taken"));
        }
        insert(data, "game");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        ArrayList<String> data = find(String.valueOf(gameID), "gameID", "game", 5);
        if (data == null){
            return null;
        }
        var serializer = new Gson();
        return new GameData(Integer.parseInt(data.get(0)), data.get(1), data.get(2), data.get(3), serializer.fromJson(data.get(4), ChessGame.class));
    }

    @Override
    public GameList listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        var serializer = new Gson();
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement(
                    "SELECT * " +
                            "FROM game;")) {
                var matches = statement.executeQuery();
                while (matches.next()) {
                    games.add(new GameData(Integer.parseInt(matches.getString(1)),
                            matches.getString(2), matches.getString(3), matches.getString(4),
                            serializer.fromJson(matches.getString(5), ChessGame.class)));
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return new GameList(games);
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException {
        if(!exists(String.valueOf(data.gameID()), "gameID", "game")){
            throw new DataAccessException("400", new DataAccessException("Error: There are no games with the game number you tried"));
        }
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement(
                    "UPDATE game " +
                            "SET whiteUsername = ?, " +
                            "blackUsername = ?, " +
                            "gameName = ?, " +
                            "game = ? " +
                            "WHERE gameID = ?;")){
                var translation = data.toSQL();
                for(int i = 1; i < 5; i++){
                    statement.setString(i, translation.get(i));
                }
                statement.setInt(5, data.gameID());
                statement.executeUpdate();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
