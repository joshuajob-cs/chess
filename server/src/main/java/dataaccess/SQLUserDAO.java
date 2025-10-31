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
        } catch (SQLException | DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }
}
