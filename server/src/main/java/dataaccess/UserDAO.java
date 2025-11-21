package dataaccess;

import model.UserData;
import server.DataAccessException;

public interface UserDAO {
    void clear() throws DataAccessException;
    void createUser(UserData data) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
