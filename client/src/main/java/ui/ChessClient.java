package ui;

import chess.ChessBoard;
import shared.DataAccessException;
import model.GameData;
import shared.ServerFacade;
import server.requests.CreateGameRequest;
import server.requests.JoinGameRequest;
import server.requests.LoginRequest;
import server.requests.RegisterRequest;
import server.results.results.LoginResult;
import server.results.results.RegisterResult;

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
                case "help" -> helpMe();
                case "quit" -> "quit";
                default -> "Please enter valid input";

            });
        } catch (DataAccessException e) {
            return "Failed " + e;
        }
    }

    public String registerUser(String... params) throws DataAccessException {
        String username = "", password = "", email = "";
        if (state != State.Prelogin) {
            return "Already logged in";
        }

        if (params.length < 3) {
            return "register requires 3 input arguments";
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
            return "Registration Failed";
        }
    }

    public String loginUser(String... params) throws DataAccessException {
        String username = "", password = "";

        if (state != State.Prelogin) {
            return "Already logged in";
        }

        if (params.length < 2) {
            return "login requires 2 input arguments";
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
            return "Login Error" ;
        }
    }

    public String logoutUser() {
        if (state != State.Postlogin) {
            return "Not logged in";
        }

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
        if (state == State.Prelogin) {
            return "Unauthorized: Please log in";
        }

        try {
            var games = facade.listGames();
            this.previousGames = games;
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
        if (state == State.Prelogin) {
            return "Unauthorized: Please log in";
        }
        String gameName = "";
        if (params.length < 1) {
            return "creategame requires argument";
        }

        gameName = params[0];

        try {
            CreateGameRequest request = new CreateGameRequest(gameName);
            var result = facade.createGame(request);
            return "Game Created: " + gameName;
        } catch (DataAccessException e) {
            return "Could not create game";
        }
    }

    public String joinGame(String... params ) throws DataAccessException {
        if (state == State.Prelogin) {
            return "Unauthorized: Please log in";
        }
        int gameID = 0;
        String gameName;
        int index;

        if (params.length < 2) {
            return "joingame requires 2 input arguments";
        }

        boolean isInteger;
        try {
            Integer.parseInt(params[1]);
            isInteger = true;
        } catch (NumberFormatException e) {
            isInteger = false;
        }

        this.playerColor = params[0].toUpperCase();
        try {
            if (isInteger) {
                index = Integer.parseInt(params[1]);
                if (index < 1 || index > previousGames.size()) {
                    return "Outside Game number range";
                }
                GameData gameData = previousGames.get(index - 1);
                this.board = gameData.game().getBoard();
                this.board.resetBoard();
                this.state = State.GamePlay;
                return "Joined game: " + index;

            } else {
                gameName = params[1];
                List<GameData> games = facade.listGames();

                for (GameData game : games) {
                    if (game.gameName().equalsIgnoreCase(gameName)) {
                        gameID = game.gameID();
                        break;
                    }
                }
                JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
                facade.joinGame(request);
                this.board = new ChessBoard();
                this.board.resetBoard();
                this.state = State.GamePlay;
                return "Joined game: " + gameName + ": " + gameID;
            }
        } catch (DataAccessException e) {
            return "JoinGame Error ";
        }
    }


    public String clearData() throws DataAccessException {
        facade.clearDatabase();
        System.out.println("Database Wiped");
        System.exit(0);
        return null;
    }

    public String observeGame(String... params) {
        if (state != State.Postlogin) {
            return "Unauthorize: not logged in";
        }

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
        this.playerColor = "WHITE";
        return "Observing " + gameData.whiteUsername() + " vs " + gameData.blackUsername();
    }

    public String helpMe() {
        return "Please type out command exactly as below";
    }

    public String help() {
        if (state == State.Prelogin) {
            return """
                    - help
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """ 
                - listgames
                - creategame <gamename>
                - joingame <playercolor> <gamename>
                - observe <gamenumber>
                - help
                - logout
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
