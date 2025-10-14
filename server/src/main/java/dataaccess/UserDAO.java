package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear();
    void createUser(UserData data) throws DataAccessException;
    UserData getUser(String username);
}
