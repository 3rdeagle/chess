package server;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import service.requests.RegisterRequest;
import service.results.RegisterResult;
import spark.*;
import service.ClearService;
import service.UserService;
import model.UserData;
import java.util.Map;

public class Server {

    private final ClearService clearService;
    private final UserService userService;

    public Server(ClearService clearService, UserService userService) {//

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
}
