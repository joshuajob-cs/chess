package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    public static Map<String, AuthData> allAuthData = new HashMap<>();

    @Override
    public void clear() {
        allAuthData.clear();
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {
        if (allAuthData.containsKey(data.authToken())){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        allAuthData.put(data.authToken(), data);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return allAuthData.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if (!allAuthData.containsKey(authToken)){
            throw new DataAccessException("401", new DataAccessException("Error: unauthorized"));
        }
        allAuthData.remove(authToken);
    }
}
