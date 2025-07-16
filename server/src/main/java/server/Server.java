package server;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import service.requests.*;
import service.results.*;
import spark.*;
import service.ClearService;
import service.UserService;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Server {

    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Server(ClearService clearService, UserService userService, GameService gameService) {//
        this.gameService = gameService;
        this.clearService = clearService;
        this.userService = userService;

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.delete("/db", this::deleteDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object deleteDatabase(Request req, Response res) {
        clearService.clear();
        res.status(200);
        return "";
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
            RegisterRequest regRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult regResult = userService.registerUser(regRequest);

            res.status(200);
            return new Gson().toJson(regResult);

    }

    private Object loginUser(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);

        res.status(200);
        return new Gson().toJson(loginResult);
    }

    private Object logoutUser(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
//        LogoutRequest logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);
        userService.logout(authToken);
        res.status(200);

        return "{}";
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        var gamesList = gameService.listGames(authToken).toArray();
        return new Gson().toJson(Map.of("games", gamesList));
    }

    private Object createGame(Request req, Response res)throws DataAccessException {
        String authToken = req.headers("Authorization");
        CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
        var game = gameService.createGames(authToken, gameName);
        var gameID = game.gameID();
        return new Gson().toJson(Map.of("gameID", gameID));
    }
}
