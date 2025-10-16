package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    public static Map<String, UserData> allUserData = new HashMap<>();

    @Override
    public void clear() {
        allUserData.clear();
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        if(data.username() == null || data.password() == null || data.email() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Bad Request"));
        }
        if (allUserData.containsKey(data.username())){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        allUserData.put(data.username(), data);
    }

    @Override
    public UserData getUser(String username) {
        return allUserData.get(username);
    }
}
