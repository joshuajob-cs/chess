package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO userMemoryAccess = new MemoryUserDAO();
    private final AuthDAO authMemoryAccess = new MemoryAuthDAO();
    private final GameDAO gameMemoryAccess = new MemoryGameDAO();

    public void clearAll(){
        userMemoryAccess.clear();
        authMemoryAccess.clear();
        gameMemoryAccess.clear();
    }
}