package dataaccess;

import model.AuthData;

import java.sql.SQLException;

import static dataaccess.SQLUtilities.SQLClear;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public void clear() {
        SQLClear("auth");
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
