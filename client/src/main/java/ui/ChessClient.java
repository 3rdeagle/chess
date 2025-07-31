package ui;

import dataaccess.DataAccessException;
import server.ServerFacade;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    enum State {Prelogin, Postlogin, GamePlay}
    private State state = State.Prelogin;
    private String username = null;

    public ChessClient(String serverUrl) {
        facade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) throws DataAccessException {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> registerUser(params);

            }
        }
    }

    public String registerUser(String... params) throws DataAccessException {
        String username, password, email;
        if (params.length >= 3) {
            state = State.Postlogin;
            username = params[0];
            password = params[1];
            email = params[2];
        }
        try {
            RegisterRequest request = new RegisterRequest(username,password,email);
            RegisterResult result = facade.registerUser(request);
            this.username = result.username();
            return "Register user" + username;
        } catch (DataAccessException e ) {
            return "Registration Failed";
        }
    }







}
