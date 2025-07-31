package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

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
    public void sampleTest() {
        Assertions.assertTrue(true);
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

}
