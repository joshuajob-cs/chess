package dataaccess;

import model.UserData;

import java.util.ArrayList;

import static dataaccess.SQLUtilities.*;

public class SQLUserDAO implements UserDAO{

    @Override
    public void clear() throws DataAccessException{
        sqlClear("user");
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        if(exists(data.username(), "username", "user")){
            throw new DataAccessException("403", new DataAccessException("Error: The username you tried has already been taken"));
        }
        insert(data, "user");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        ArrayList<String> data = find(username, "username", "user", 3);
        if (data == null){
            return null;
        }
        return new UserData(data.get(0), data.get(1), data.get(2));
    }
}
