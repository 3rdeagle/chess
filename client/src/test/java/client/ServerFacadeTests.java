package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import server.results.CreateGameResult;
import server.requests.CreateGameRequest;
import server.requests.JoinGameRequest;
import server.requests.LoginRequest;
import server.requests.RegisterRequest;
import server.results.results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        facade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerUserPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "BobsPass", "Bobby1@gmail.com");
        RegisterResult result = facade.registerUser(request);

        assertNotNull(result.authToken());
        assertEquals("Bob", result.username());
    }

    @Test
    public void registerUserNegative() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Bob", "BobsPass", "Bobby1@gmail.com");
        RegisterResult result = facade.registerUser(request);

        RegisterRequest request2 = new RegisterRequest("Bob", "BobsPass", "Bobby1@gmail.com");

        assertThrows(DataAccessException.class, () -> facade.registerUser(request2));
    }

    @Test
    public void loginUserPositive() throws DataAccessException {
        facade.registerUser(new RegisterRequest("Hannah", "password123", "hannah@gmail.com"));
        LoginRequest request = new LoginRequest("Hannah", "password123");
        assertNotNull(facade.login(request));
    }

    @Test
    public void loginUserNegative() throws DataAccessException {
        facade.registerUser(new RegisterRequest("Hannah", "password123", "hannah@gmail.com"));
        LoginRequest request = new LoginRequest("Hannah", "password1234");
        assertThrows(DataAccessException.class, () -> facade.login(request));
    }

    @Test
    public void logoutUserPositive() throws DataAccessException {
        facade.registerUser(new RegisterRequest("Hannah", "password123", "hannah1@gmail.com"));
        assertDoesNotThrow(() -> facade.logout());

    }

    @Test
    public void logoutUserNegative() {
        assertThrows(DataAccessException.class, () -> facade.logout()); // can't logout if never logged in
    }

    @ Test
    public void createGamePositive() throws DataAccessException {
        facade.registerUser(new RegisterRequest("Obi-Wan",
                "HelloThere", "GeneralKenobi@gmail.com"));
        CreateGameRequest request = new CreateGameRequest("CloneWar1");
        assertDoesNotThrow(() -> facade.createGame(request));
    }

    @Test
    public void createGameNegative() {
        CreateGameRequest request = new CreateGameRequest("Badone");
        assertThrows(DataAccessException.class, ()-> facade.createGame(request));
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        facade.registerUser(new RegisterRequest("Anakin", "Chosen1", "theChosen1@gmail.com"));
        facade.createGame(new CreateGameRequest("game1"));
        facade.createGame(new CreateGameRequest("game2"));
        var result = facade.listGames();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, ()-> facade.listGames());
    }

    @Test
    public void joinGamePositive() throws DataAccessException {
        facade.registerUser(new RegisterRequest("Billy", "password", "Testgmail"));
        CreateGameResult game = facade.createGame(new CreateGameRequest("game1"));
        JoinGameRequest request = new JoinGameRequest("WHITE", game.gameID());
        assertDoesNotThrow(()-> facade.joinGame(request));
    }

    @Test
    public void joinGameNegative() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest("WHITE", 123);
        assertThrows(DataAccessException.class, ()-> facade.joinGame(request)  );
    }

    @Test
    public void clearDatabasePositive() throws DataAccessException {
        facade.registerUser(new RegisterRequest("bobo", "bobopass", "boboemail"));
        facade.createGame(new CreateGameRequest("TestGame"));
        facade.clearDatabase();
        assertThrows(DataAccessException.class, ()->
                facade.login(new LoginRequest("bobo", "bobopass")));

    }






}
