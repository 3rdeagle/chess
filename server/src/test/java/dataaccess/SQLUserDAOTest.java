package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;
class SQLUserDAOTest {

    private static SQLUserDAO sqlUserDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        new SQLAuthDAO().clearAuth();
        sqlUserDAO = new SQLUserDAO();
    }

    @BeforeEach
    void clearTable() throws DataAccessException {
        sqlUserDAO.clearUsers();
    }

    @Test
    void clearUsersPositive() throws DataAccessException {
        UserData user = new UserData("Tester", "testpass", "test@gmail.com");
        sqlUserDAO.createUser(user);
        assertNotNull(sqlUserDAO.getUser("Tester"));
        sqlUserDAO.clearUsers();
        assertNull(sqlUserDAO.getUser("Tester"));
    }

    @Test
    void createUserPositive() throws DataAccessException {
        UserData user = new UserData("Hannah", "testpass", "test@gmail.com");
        sqlUserDAO.createUser(user);
        UserData testUser = sqlUserDAO.getUser("Hannah");
        assertNotNull(testUser);
        assertEquals("test@gmail.com", testUser.email());
    }

    @Test
    void createUserNegative() throws DataAccessException {
        UserData user = new UserData("Hannah", "testpass1", "test@gmail.com");
        sqlUserDAO.createUser(user);
        UserData sameUser = new UserData("Hannah", "testpass1", "test@gmail.com");
        assertThrows(DataAccessException.class, () -> sqlUserDAO.createUser(sameUser));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        UserData user = new UserData("Tester", "testpass", "test@gmail.com");
        sqlUserDAO.createUser(user);
        UserData calledUser = sqlUserDAO.getUser("Tester");
        assertNotNull(calledUser);
        assertEquals("Tester", calledUser.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        UserData calledUser = sqlUserDAO.getUser("NotHannah");
        assertNull(calledUser);
    }
}