package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import service.GameService;
import service.requests.*;
import service.results.*;
import spark.*;
import service.ClearService;
import service.UserService;
import java.util.Collection;
import java.util.Map;

public class Server {
    private GameService gameService;
    private ClearService clearService;
    private UserService userService;

    public Server() {
        UserDao userDao = new MemoryUserDAO();
        AuthDAO authDao  = new MemoryAuthDAO();
        GameDAO gameDao  = new MemoryGameDAO();

        this.gameService = new GameService(authDao,gameDao);
        this.clearService = new ClearService(userDao,authDao,gameDao);
        this.userService = new UserService(userDao, authDao);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        try {
            UserDao sqlUserDao = new SQLUserDAO();
            AuthDAO sqlAuthDao = new SQLAuthDAO();
            GameDAO sqlGameDao = new SQLGameDAO();

            this.userService = new UserService(sqlUserDao, sqlAuthDao);
            this.clearService = new ClearService(sqlUserDao, sqlAuthDao, sqlGameDao);
            this.gameService = new GameService(sqlAuthDao, sqlGameDao);

        } catch (DataAccessException e) {
            String error = e.getMessage();
            System.err.println("SQL failed");
        }

        Spark.staticFiles.location("web");

        //endpoints and handle exceptions here.
        Spark.delete("/db", this::deleteDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    // Handlers for the data
    private Object deleteDatabase(Request req, Response res) throws DataAccessException {
        try {
            clearService.clear();
            res.status(200);
            return "";
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: Internal Server Error"));
        }
    }

    private Object registerUser(Request req, Response res) {
        try {
            RegisterRequest regRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            if (regRequest == null || regRequest.password() == null ||
                    regRequest.email() == null || regRequest.username() == null) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: bad request"));
            }
            RegisterResult regResult = userService.registerUser(regRequest);

            res.status(200);
            return new Gson().toJson(regResult);
        } catch (DataAccessException e) {
            String error = e.getMessage();
            if (error.contains("Already Taken") || error.contains("Unauthorized") ||
                    error.contains("No such username") || error.contains("Wrong Password")) {
                res.status(403);
                return new Gson().toJson(Map.of("message", "Error Already Taken"));
            } else {
                res.status(500);
                return new Gson().toJson(Map.of("message", "Error: Internal Server Error"));
            }

        }
    }
    
    private Object loginUser(Request req, Response res) {
        try {
            LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
            if (loginRequest == null || loginRequest.password() == null || loginRequest.username() == null) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: bad request"));
            }
            LoginResult loginResult = userService.login(loginRequest);

            res.status(200);
            return new Gson().toJson(loginResult);
        } catch (DataAccessException e) {
            String error = e.getMessage();
            if (error.contains("Unauthorized") || error.contains("No such username")
                    || error.contains("Wrong Password")) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: Unauthorized"));
            } else {
                res.status(500);
                return new Gson().toJson(Map.of("message", "Error: Internal Server Error"));
            }
        }
    }

    private Object logoutUser(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            userService.logout(authToken);
            res.status(200);
            return "{}";

        } catch (DataAccessException e) {
            return handleExceptions(e, res);
        }
    }

    private Object listGames(Request req, Response res)  {
        try {
            String authToken = req.headers("Authorization");
            Collection<GameData> gamesList = gameService.listGames(authToken);
            res.status(200);
            return new Gson().toJson(Map.of("games", gamesList));
        } catch (DataAccessException e) {
            return handleExceptions(e, res);
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
            if (gameName == null || gameName.gameName() == null) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: bad request"));
            }
            var game = gameService.createGames(authToken, gameName);
            var gameID = game.gameID();
            return new Gson().toJson(Map.of("gameID", gameID));
        } catch (DataAccessException e) {
            return handleExceptions(e, res);
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            JoinGameRequest gameName = new Gson().fromJson(req.body(), JoinGameRequest.class);
            if (gameName == null || gameName.playerColor() == null || gameName.gameID() <= 0 ||
                    (!gameName.playerColor().equals("WHITE") && !gameName.playerColor().equals("BLACK"))) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: bad request"));
            }
            gameService.joinGame(authToken, gameName);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            String error = e.getMessage();
            if (error.contains("already taken") || error.contains("Full")) {
                res.status(403);
                return new Gson().toJson(Map.of("message", "Error: already taken"));
            }
            if (error.contains("Unauthorized")) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: Unauthorized"));
            } else {
                res.status(500);
                return new Gson().toJson(Map.of("message", "Error: Internal Server Error"));
            }
        }
    }

    private String handleExceptions(DataAccessException e, Response res) {
            String error = e.getMessage();
            if (error.contains("Unauthorized")) {
                res.status(401);
                return new Gson().toJson(Map.of("message", "Error: Unauthorized"));
            } else {
                res.status(500);
                return new Gson().toJson(Map.of("message", "Error: Internal Server Error"));
            }
    }
}
