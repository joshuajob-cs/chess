package dataaccess;

import model.AuthData;
import server.DataAccessException;

public interface AuthDAO {
    void clear() throws DataAccessException;
    void createAuth(AuthData data) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
