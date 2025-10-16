package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DataAccessTest {
    @Test
    void clear() throws DataAccessException{
        var user = new UserData("joe", "12345", "j@j");
        UserDAO da = new MemoryUserDAO();
        assertNull(da.getUser(user.username()));
        da.createUser(user);
        assertNotNull(da.getUser(user.username()));
        da.clear();
        assertNull(da.getUser(user.username()));
    }
}
