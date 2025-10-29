package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public void clear() {

    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
