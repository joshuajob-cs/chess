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
        if(data.authToken() == null || data.username() == null){
            throw new DataAccessException("400", new DataAccessException("Error: Bad Request"));
        }
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
    public AuthData getAuthWithUsername(String username) {
        for(AuthData auth: allAuthData.values()){
            if (username.equals(auth.username())){
                return auth;
            }
        }
        return null;
    }
}
