package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
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
    void registerUserPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1324", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        assertEquals("Hannah", result.username());
        assertNotNull(result.authToken());

    }
    @Test
    void registerUserNegative() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1324", "hannah@gmail.com");
        userService.registerUser(req);
        assertThrows(DataAccessException.class, () ->
                userService.registerUser(new RegisterRequest("Hannah", "pass1324", "hannah@gmail.com")));
    }

    @Test
    void loginPositive() throws DataAccessException{
        RegisterRequest req = new RegisterRequest("bobby1", "bobbypassword1", "bobby@gmail.com");
        userService.registerUser(req);

        LoginRequest request = new LoginRequest("bobby1", "bobbypassword1");
        LoginResult result = userService.login(request);
        assertEquals("bobby1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginNegative() throws DataAccessException{
        RegisterRequest req = new RegisterRequest("bobby1", "bobbypassword1", "bobby@gmail.com");
        userService.registerUser(req);

        LoginRequest request = new LoginRequest("bobby1", "bobbypassword4");
        assertThrows(DataAccessException.class, () -> userService.login(new LoginRequest("bobby1", "bobbywrong")));
    }

    @Test
    void logoutPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        userService.logout(result.authToken());
        assertNull(authDao.getAuth(result.authToken()));
    }

    @Test
    void logoutNegative() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("Hannah", "pass1234", "hannah@gmail.com");
        RegisterResult result = userService.registerUser(req);
        assertThrows(DataAccessException.class, () -> userService.logout("FakeAuth"));
    }
}