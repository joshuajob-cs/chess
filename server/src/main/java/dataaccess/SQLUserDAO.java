package dataaccess;

import model.UserData;

import java.util.ArrayList;

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
        insert(data, "user");
    }

    @Override
    public UserData getUser(String username) {
        ArrayList<String> data = find(username, "username", "user", 3);
        if (data == null){
            return null;
        }
        return new UserData(data.get(0), data.get(1), data.get(2));
    }
}
