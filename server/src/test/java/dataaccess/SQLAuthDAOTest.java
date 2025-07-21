package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private static SQLAuthDAO authDAO;

    @BeforeAll
    static void initiateDatabase() throws DataAccessException {

        authDAO = new SQLAuthDAO();
    }

    @BeforeEach
    void clearTable() throws DataAccessException {
        authDAO.clearAuth();
    }
    @Test
    void clearAuth() {

    }

    @Test
    void createAuthPositive() throws DataAccessException {
        AuthData testData = new AuthData("TestToken123", "TestUsername");
        authDAO.createAuth(testData);
    }

    @Test
    void getAuth() {
    }

    @Test
    void deleteAuth() {
    }
}