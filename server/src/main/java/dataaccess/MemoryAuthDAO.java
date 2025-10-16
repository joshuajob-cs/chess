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

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }
}
