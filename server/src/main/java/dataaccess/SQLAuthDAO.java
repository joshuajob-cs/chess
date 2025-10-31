package dataaccess;

import model.AuthData;

import java.sql.SQLException;

import static dataaccess.SQLUtilities.SQLClear;
import static dataaccess.SQLUtilities.exists;

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
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement(
                    "INSERT INTO user " +
                            "VALUES (?, ?);")){
                statement.setString(1, data.authToken());
                statement.setString(2, data.username());
                statement.executeUpdate();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
