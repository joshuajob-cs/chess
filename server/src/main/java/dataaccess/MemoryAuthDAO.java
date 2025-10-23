package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    public static Map<String, AuthData> data = new HashMap<>();

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void createAuth(AuthData newData) throws DataAccessException {
        if (data.containsKey(newData.authToken())){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        data.put(newData.authToken(), newData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return data.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if (!data.containsKey(authToken)){
            throw new DataAccessException("401", new DataAccessException("Error: unauthorized"));
        }
        data.remove(authToken);
    }
}
