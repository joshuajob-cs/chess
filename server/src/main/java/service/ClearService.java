package service;

import dataaccess.*;
import server.DataAccessException;

public class ClearService {
    private final UserDAO userMemoryAccess = new SQLUserDAO();
    private final AuthDAO authMemoryAccess = new SQLAuthDAO();
    private final GameDAO gameMemoryAccess = new SQLGameDAO();

    public void clearAll() throws DataAccessException {
        userMemoryAccess.clear();
        authMemoryAccess.clear();
        gameMemoryAccess.clear();
    }
}