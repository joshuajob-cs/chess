package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final UserDAO userMemoryAccess = new MemoryUserDAO();

    public void clearAll(){
        userMemoryAccess.clear();
    }
}
