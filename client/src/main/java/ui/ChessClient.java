package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import server.requests.JoinGameRequest;
import shared.DataAccessException;
import model.GameData;
import shared.ServerFacade;
import server.requests.CreateGameRequest;
import server.requests.LoginRequest;
import server.requests.RegisterRequest;
import server.results.results.LoginResult;
import server.results.results.RegisterResult;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.ConnectCommand;

import java.io.IOException;
import java.util.*;

public class ChessClient {
    private final ServerFacade facade;
    private final String serverUrl;
    private ChessBoard board;
    enum State {Prelogin, Postlogin, GamePlay}
    private State state = State.Prelogin;
    private String username = null;
    private String playerColor;
    private List<GameData> previousGames = List.of();
    private WebSocketFacade webSocket;
    private NotificationHandler notificationHandler;
    private String authToken;
    private ChessGame game;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        facade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;

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
                // gameplay
                case "move" -> makeMove(params);
                case "leave" -> leaveGame();
                case "resign" -> resignGame();
                case "showmoves" -> showMoves(params);
                case "quit" -> "quit";
                default -> "Please enter valid input";

            });
        } catch (DataAccessException | IOException e) {
            return "Failed " + e;
        }
    }

    private String showMoves(String... params) {
        if (state != State.GamePlay) {
            return "Not in a game bud";
        }
        if (params.length != 1) {
            return "Usage: showmoves <pieceposition>";
        }

        ChessPosition position;
        try {
            position = ChessMove.convertToCoor(params[0]);
        } catch (Exception e) {
            return "invalid position";
        }

        Collection<ChessMove> possibleMoves = game.validMoves(position);
        if (possibleMoves.isEmpty()) {
            return "No possible moves for this piece";
        }

        Set<ChessPosition> targetMoves = new HashSet<>();
        for (ChessMove m : possibleMoves) {
            targetMoves.add(m.getEndPosition()); // get list of all possilbe moves end positions
        }

        String color = (playerColor != null ? playerColor : "WHITE");
        ChessBoardPrinter.print(this.board, color, targetMoves);
        return "";

    }

    private String resignGame() throws IOException {
        webSocket.sendResign();
        webSocket.closeSession();
        state = State.Postlogin;
        return "";
    }

    private String leaveGame() throws IOException {
        webSocket.sendLeave();
        webSocket.closeSession();
        state = State.Postlogin;
        return "";
    }

    private String makeMove(String... params) throws IOException, DataAccessException {

        ChessMove moveToMake = ChessMove.determineMove(params);
        webSocket.sendMove(moveToMake);
        return "";
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
            this.authToken = result.authToken();
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
            this.authToken = result.authToken();
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

    public String joinGame(String... params ) throws DataAccessException, IOException {
        if (state == State.Prelogin) {
            return "Unauthorized: Please log in";
        }

        int index;

        if (params.length < 2) {
            return "joingame requires 2 input arguments";
        }

        String color = params[0].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Invalid color";
        }

        try {
            index = Integer.parseInt(params[1]);
            if (index < 1 || index > previousGames.size()) {
                return "Outside Game number range";
            }
        } catch (NumberFormatException e) {
            return "Give a valid number";
        }

        GameData gameData = previousGames.get(index - 1);
        try {
            JoinGameRequest joinGameRequest = new JoinGameRequest(color, gameData.gameID());
            facade.joinGame(joinGameRequest);
        } catch (DataAccessException e) {
            return "Join game error";
        }

        if (color.equals("WHITE") && gameData.whiteUsername() != null) {
            return "White taken";
        }

        if (color.equals("BLACK") && gameData.blackUsername() != null) {
            return "Black taken";
        }

        this.playerColor = color;
        this.game = gameData.game();
        this.board = gameData.game().getBoard();
        this.board.resetBoard();

        // open a websocket after the user has joined a game
        this.webSocket = new WebSocketFacade(serverUrl, notificationHandler, authToken, gameData.gameID());
        this.webSocket.sendConnection();
        this.state = State.GamePlay;

        return "Joined game: " + index;

    }


    public String clearData() throws DataAccessException {
        facade.clearDatabase();
        System.out.println("Database Wiped");
        System.exit(0);
        return null;
    }

    public String observeGame(String... params) throws IOException {
        if (state != State.Postlogin) {
            return "Unauthorize: not logged in";
        }
        try {
            this.previousGames = facade.listGames();
        } catch (DataAccessException e) {
            return "Failed update games";
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
        this.game = gameData.game();
        this.playerColor = "WHITE";
        this.webSocket = new WebSocketFacade(serverUrl, notificationHandler, authToken, gameData.gameID());
        this.webSocket.sendConnection();
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
        } else if (state == State.Postlogin) {
            return """ 
                    - listgames
                    - creategame <gamename>
                    - joingame <playercolor> <gamenumber>
                    - observe <gamenumber>
                    - help
                    - logout
                    - quit
                    """;
        } else {
            return """ 
                    - move <start Position> <EndPosition>
                    - resign
                    - leave
                    - show moves
                    - help
                    - logout
                    - quit
                    """;
        }
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

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
}
