package dataaccess;

import model.UserData;
import server.DataAccessException;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    public static Map<String, UserData> data = new HashMap<>();

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void createUser(UserData newData) throws DataAccessException {
        if (data.containsKey(newData.username())){
            throw new DataAccessException("403", new DataAccessException("Error: You are not authorized. Please log in."));
        }
        data.put(newData.username(), newData);
    }

    @Override
    public UserData getUser(String username) {
        return data.get(username);
    }
}
