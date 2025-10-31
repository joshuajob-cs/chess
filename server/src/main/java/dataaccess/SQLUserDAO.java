package dataaccess;

import model.UserData;

import java.sql.SQLException;

import static dataaccess.SQLUtilities.*;

public class SQLUserDAO implements UserDAO{

    @Override
    public void clear(){
        SQLClear("user");
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        if(exists(data.username(), "username", "user")){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        try(var conn = DatabaseManager.getConnection()) {
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
        try(var conn = DatabaseManager.getConnection()) {
            try(var statement = conn.prepareStatement(
                    "SELECT * " +
                        "FROM user " +
                        "WHERE username = ?;")){
                statement.setString(1, username);
                var matches = statement.executeQuery();
                if (matches.next()){
                    return new UserData(username, matches.getString(2), matches.getString(3));
                }
                else{
                    return null;
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
