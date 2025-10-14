package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    Map<String, UserData> allUserData = new HashMap<>();

    @Override
    public void clear() {
        allUserData.clear();
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        if (allUserData.containsKey(data.username())){
            throw new DataAccessException("Error: already taken");
        }
        allUserData.put(data.username(), data);
    }

    @Override
    public UserData getUser(String username) {
        return allUserData.get(username);
    }
}
