package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.requests.CreateGameRequest;
import server.requests.RegisterRequest;
import server.results.results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;
class ClearServiceTest {

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
    void clear() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);

        CreateGameRequest gameRequest = new CreateGameRequest("Test");
        GameData game = gameService.createGames(result.authToken(), gameRequest);

        clearService.clear();

        GameData updated = gameDao.getGame(game.gameID());
        assertNull(updated);
    }
}