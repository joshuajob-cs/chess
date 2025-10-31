package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    @Override
    public void clear(){
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement("TRUNCATE TABLE user;")){
                statement.executeUpdate();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement(
                    "SELECT username " +
                        "FROM user " +
                        "WHERE username = ?;")){
                statement.setString(1, data.username());
                var matches = statement.executeQuery();
                if (matches.next()){
                    throw new DataAccessException("403", new DataAccessException("Error: already taken"));
                }
            }
            try(var statement = conn.prepareStatement(
                    "INSERT INTO user " +
                            "VALUES (?, ?, ?);")){
                statement.setString(1, data.username());
                statement.setString(2, data.password());
                statement.setString(3, data.email());
                statement.executeUpdate();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }
}
