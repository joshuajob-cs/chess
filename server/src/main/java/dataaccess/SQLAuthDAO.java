package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public void clear() {
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement("TRUNCATE TABLE auth;")){
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
