package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear();
    void createAuth(AuthData data) throws DataAccessException;
    AuthData getAuth(String authToken);
}
