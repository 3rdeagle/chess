package ui;

import chess.ChessBoard;
import dataaccess.DataAccessException;
import model.GameData;
import server.ServerFacade;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.util.Arrays;
import java.util.List;

public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    private ChessBoard board;
    enum State {Prelogin, Postlogin, GamePlay}
    private State state = State.Prelogin;
    private String username = null;
    private String playerColor;
    private List<GameData> previousGames = List.of();

    public ChessClient(String serverUrl) {
        facade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;

    }

    public String eval(String input) throws DataAccessException {
        try {
            var tokens = input.split(" ");
            var cmd = tokens[0].toLowerCase();
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return String.valueOf(switch (cmd) {
                case "register" -> registerUser(params);
                case "login" -> loginUser(params);
                case "logout" -> logoutUser();
                case "listgames" -> listGames();
                case "creategame" -> createGame(params);
                case "joingame" -> joinGame(params);
                case "forgetmestick" -> clearData();
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();

            });
        } catch (DataAccessException e) {
            return "Failed " + e;
        }
    }

    public String registerUser(String... params) throws DataAccessException {
        String username = "", password = "", email = "";

        if (params.length < 3) {
            return "register <username> <password> <email>";
        }
        username = params[0];
        password = params[1];
        email = params[2];
        try {
            RegisterRequest request = new RegisterRequest(username,password,email);
            RegisterResult result = facade.registerUser(request);
            this.username = result.username();
            state = State.Postlogin;
            return "Register user: " + username;
        } catch (DataAccessException e ) {
            return "Registration Failed" + e.getMessage();
        }
    }

    public String loginUser(String... params) throws DataAccessException {
        String username = "", password = "";
        if (params.length < 2) {
            return "login <username> <password>";
        }

        username = params[0];
        password = params[1];

        try {
            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = facade.login(request);
            this.username = result.username();
            state = State.Postlogin;
            return "Login Successful";
        } catch (DataAccessException e) {
            return "Login Error";
        }
    }

    public String logoutUser() {
        try {
            facade.logout();
            this.username = null;
            state = State.Prelogin;
            return "Logged out succesful";
        } catch (DataAccessException e) {
            return "Logout Error";
        }
    }

    public String listGames() {
        try {
            var games = facade.listGames();
            this.previousGames = games;
//            System.out.println(games);
            if (games == null || games.isEmpty()) {
                return "No games";
            }
            StringBuilder stringBuilder = new StringBuilder();
            int i = 1;
            for (var game : games) {
                stringBuilder.append(i++);
                stringBuilder.append(". ");
                stringBuilder.append("Name: ").append(game.gameName());
                stringBuilder.append(" White: ").append(game.whiteUsername());
                stringBuilder.append(" Black: ").append(game.blackUsername());
                stringBuilder.append(" \n");
            }
            return stringBuilder.toString();
        } catch (DataAccessException e) {
            return "List Game error ";
        }
    }

    public String createGame(String... params) {
        String gameName = "";
        if (params.length < 1) {
            return "creategame <gameName>";
        }

        gameName = params[0];

        try {
            CreateGameRequest request = new CreateGameRequest(gameName);
            var result = facade.createGame(request);
            return "Game Created: " + result.gameID();
        } catch (DataAccessException e) {
            return "Could not create game";
        }
    }

    public String joinGame(String... params ) throws DataAccessException {
        int gameID = 0;
        String gameName;

        if (params.length < 2) {
            return "joingame <playercolor> <gamename>";
        }

        this.playerColor = params[0].toUpperCase();
        gameName = params[1];

        List<GameData> games = facade.listGames();

        for (GameData game : games) {
            if (game.gameName().equalsIgnoreCase(gameName)) {
                gameID = game.gameID();
                break;
            }
        }

        try {
            JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
            facade.joinGame(request);
            this.board = new ChessBoard();
            this.board.resetBoard();
            this.state = State.GamePlay;
            return "Joined game: " + gameID;
        } catch (DataAccessException e) {
            return "JoinGame Error: " + e.getMessage();
        }
    }

    public String clearData() throws DataAccessException {
        facade.clearDatabase();
        System.out.println("Database Wiped");
        System.exit(0);
        return null;
    }

    public String observeGame(String... params) {
        if (params.length < 1) {
            return "Need observe <gamenumber>";
        }
        int index;
        try {
            index = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "Give a valid number";
        }
        if (index < 1 || index > previousGames.size()) {
            return "Outside Game number range";
        }
        GameData gameData = previousGames.get(index-1);
        this.board = gameData.game().getBoard();
        this.state = State.GamePlay;
        ChessBoardPrinter.main();
        return "Observing " + gameData.whiteUsername() + " vs " + gameData.blackUsername();
    }

    public String help() {
        if (state == State.Prelogin) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """ 
                - listgames
                - creategame <gamename>
                - joingame <playercolor> <gamename>
                - logout
                - observe
                - quit
                """;
    }

    public State getState() {
        return state;
    }

    public ChessBoard getBoard(){
        return board;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public List<GameData> getPreviousGames() {
        return previousGames;
    }
}
