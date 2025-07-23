package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private static SQLAuthDAO sqlauthDAO;
    private static SQLUserDAO sqlUserDAO;

    @BeforeAll
    static void initiateDatabase() throws DataAccessException {
        sqlauthDAO = new SQLAuthDAO();
        sqlUserDAO = new SQLUserDAO();
    }

    @BeforeEach
    void clearTable() throws DataAccessException {
        sqlauthDAO.clearAuth();
        sqlUserDAO.clearUsers();
    }

    @Test
    void clearAuth() throws DataAccessException {
        UserData testUser = new UserData("Dave", "Manning", "test@gmail.com");
        sqlUserDAO.createUser(testUser);
        UserData testUser2 = new UserData("Dave2", "Bobby", "bobby@gmail.com");
        sqlUserDAO.createUser(testUser2);

        AuthData testData = new AuthData("TestToken1", "Dave");
        AuthData testData2 = new AuthData("TestToken2", "Dave2");

        sqlauthDAO.createAuth(testData);
        sqlauthDAO.createAuth(testData2);

        assertNotNull(sqlauthDAO.getAuth(testData.authToken()));
        assertNotNull(sqlauthDAO.getAuth(testData2.authToken()));

        sqlauthDAO.clearAuth();

        assertThrows(DataAccessException.class, () -> sqlauthDAO.getAuth("TestToken1"));
        assertThrows(DataAccessException.class, () -> sqlauthDAO.getAuth("TestToken2"));
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        UserData testUser = new UserData("TestUsername", "test", "test@gmail.com");
        sqlUserDAO.createUser(testUser);

        AuthData testData = new AuthData("TestToken123", "TestUsername");
        assertDoesNotThrow(() -> sqlauthDAO.createAuth(testData));
        AuthData getData = sqlauthDAO.getAuth(testData.authToken());
        assertEquals(testData.authToken(), getData.authToken());
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        AuthData testData = new AuthData("TestToken123", "TestUsername");
        assertThrows(DataAccessException.class, () -> sqlauthDAO.createAuth(testData));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        UserData testUser = new UserData("Hannah", "test", "test@gmail.com");
        sqlUserDAO.createUser(testUser);

        AuthData data = new AuthData("HannahToken", "Hannah");
        sqlauthDAO.createAuth(data);
        assertNotNull(sqlauthDAO.getAuth(data.authToken()));
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> sqlauthDAO.getAuth("bob"));
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        UserData testUser = new UserData("Dave", "Manning", "test@gmail.com");
        sqlUserDAO.createUser(testUser);

        AuthData testData = new AuthData("TestToken12", "Dave");
        sqlauthDAO.createAuth(testData); // create auth data
        assertNotNull(sqlauthDAO.getAuth(testData.authToken())); // get the authdata and make sure not null
        sqlauthDAO.deleteAuth("TestToken12"); // delete that authdata
        assertThrows(DataAccessException.class, () -> sqlauthDAO.getAuth("TestToken12")); //no longer there
    }

    @Test
    void deleteAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> sqlauthDAO.deleteAuth("Fake"));
    }
}