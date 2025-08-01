package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.requests.CreateGameRequest;
import shared.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    private static SQLGameDAO sqlGameDAO;

    @BeforeAll
    static void setup() throws DataAccessException {

        sqlGameDAO = new SQLGameDAO();
    }

    @BeforeEach
    void clearTable() throws DataAccessException {
        sqlGameDAO.clearGames();
    }

    @Test
    void clearGamesPositive() throws DataAccessException {
        sqlGameDAO.createGame(new CreateGameRequest("Notempty"));
        assertFalse(sqlGameDAO.listGames().isEmpty());
        sqlGameDAO.clearGames();
        assertTrue(sqlGameDAO.listGames().isEmpty());
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        GameData testGame = sqlGameDAO.createGame(new CreateGameRequest("TestName"));
        GameData testGame1 = sqlGameDAO.createGame(new CreateGameRequest("TestName1"));
        GameData testGame2 = sqlGameDAO.createGame(new CreateGameRequest("TestName2"));
        assertNotNull(sqlGameDAO.listGames());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        var games = sqlGameDAO.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void createGamePositive() throws DataAccessException {
        String gameName = "TestGame";
        CreateGameRequest request = new CreateGameRequest(gameName);

        GameData createdGame = sqlGameDAO.createGame(request);
        assertNotNull(createdGame);
        assertTrue(createdGame.gameID() > 0);
        assertNull(createdGame.whiteUsername());
        assertNull(createdGame.blackUsername());
        assertEquals(gameName, createdGame.gameName());
    }

    @Test
    void createGamesNegative() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest(null);
        assertThrows(DataAccessException.class, () -> sqlGameDAO.createGame(request));
    }

    @Test
    void getGamePositive() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("TestGame");
        GameData createdGame = sqlGameDAO.createGame(request);
        GameData calledGame = sqlGameDAO.getGame(createdGame.gameID());
        assertNotNull(calledGame);
        assertEquals(createdGame.gameID(), calledGame.gameID());
        assertEquals(createdGame.gameName(), calledGame.gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("TestGame");
        GameData createdGame = sqlGameDAO.createGame(request);
        GameData result = sqlGameDAO.getGame(3);
        assertNull(result);
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        GameData testGame = sqlGameDAO.createGame(new CreateGameRequest("TestName"));
        GameData update = new GameData(testGame.gameID(), "Bob", "Bill", testGame.gameName(), testGame.game());
        sqlGameDAO.updateGame(update);
        GameData updatedGame = sqlGameDAO.getGame(testGame.gameID());
        assertNotEquals(testGame.whiteUsername(), updatedGame.whiteUsername());
        assertNotEquals(testGame.blackUsername(), updatedGame.blackUsername());
        //Maybe test for move??
    }

    @Test
    void updateGameNegative() throws DataAccessException {
        GameData testGame = sqlGameDAO.createGame(new CreateGameRequest("TestName"));
        GameData badUpdate = new GameData(testGame.gameID(), "Bob", "Bill", null, testGame.game());
        assertThrows(DataAccessException.class, () -> sqlGameDAO.updateGame(badUpdate));
    }
}