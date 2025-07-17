package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

import javax.xml.crypto.Data;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService gameService;
    private ClearService clearService;
    private UserService userService;
    private UserDao userDao;
    private GameDAO gameDao;
    private AuthDAO authDao;

    @BeforeEach
    void setUp() {
        userDao = new MemoryUserDAO();
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();

        gameService = new GameService(authDao,gameDao);
        clearService = new ClearService(userDao,authDao,gameDao);
        userService = new UserService(userDao, authDao);
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        Collection<GameData> results = gameService.listGames(result.authToken());
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        assertThrows(DataAccessException.class, () -> gameService.listGames("FakeAuth"));
    }

    @Test
    void createGamesPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        CreateGameRequest gameRequest = new CreateGameRequest("Test");
        GameData game = gameService.createGames(result.authToken(), gameRequest);
        assertNotNull(game);
        assertEquals(1, game.gameID());
    }

    @Test
    void createGamesNegative() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        CreateGameRequest gameRequest = new CreateGameRequest("Test");
        assertThrows(DataAccessException.class, () -> gameService.createGames("FakeAuth", gameRequest));
    }


    @Test
    void joinGamePositive() throws DataAccessException{
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);

        CreateGameRequest testGameRequest = new CreateGameRequest("Testing");
        GameData newGame = gameService.createGames(result.authToken(), testGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", newGame.gameID());
        gameService.joinGame(result.authToken(), joinGameRequest);

        GameData updated =gameDao.getGame(newGame.gameID());
        assertEquals("Hannah", updated.whiteUsername());
    }

    @Test
    void joinGameNegative() throws DataAccessException{
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);

        CreateGameRequest testGameRequest = new CreateGameRequest("Testing");
        GameData newGame = gameService.createGames(result.authToken(), testGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", newGame.gameID());
        gameService.joinGame(result.authToken(), joinGameRequest);
        assertThrows(DataAccessException.class, () -> gameService.joinGame(result.authToken(), joinGameRequest));
    }

    @Test
    void checkAuthorizationPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        String authToke = result.authToken();
        assertDoesNotThrow(() -> gameService.checkAuthorization(authToke));
    }

    @Test
    void checkAuthorizationNegative() throws DataAccessException {
        String authToke = "UnauthorizedUser";
        assertThrows(DataAccessException.class, () -> gameService.checkAuthorization(authToke));
    }
}