package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.SQLUtilities.*;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public void clear() {
        SQLClear("auth");
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {
        if(exists(data.authToken(), "authToken", "auth")){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        insert(data, "auth");
    }

    @Override
    public AuthData getAuth(String authToken) {
        ArrayList<String> data = find(authToken, "authToken", "auth", 2);
        if (data == null){
            return null;
        }
        return new AuthData(data.get(0), data.get(1));
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(!exists(authToken, "authToken", "auth")){
            throw new DataAccessException("401", new DataAccessException("Error: unauthorized"));
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM auth " +
                            "WHERE authToken = ?;")) {
                statement.setString(1, authToken);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
