package dataaccess;

import model.AuthData;

import static dataaccess.SQLUtilities.*;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public void clear() {
        SQLClear("auth");
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {
        if(exists(data.authToken(), "authToken", "auth")){
            throw new DataAccessException("403", new DataAccessException("Error: already taken"));
        }
        insert(data, "auth");
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
