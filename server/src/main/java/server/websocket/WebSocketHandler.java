package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import service.GameService;
import shared.DataAccessException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import java.io.IOException;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final GameService gameService;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.gameService = new GameService(authDAO, gameDAO);
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        var action = jsonObject.get("commandType").getAsString();
        switch (action) {
            case "CONNECT" -> connect(session, message);
            case "MAKE_MOVE" -> makeMove(session, message);
            case "LEAVE" -> leave(session, message);
            case "RESIGN" -> resign(session, message);
        }
    }

    private void resign(Session session, String message) {
        ResignCommand command = null;
        try {
            command = new Gson().fromJson(message, ResignCommand.class);
            checkCommand(command.authToken, command.gameID);

            GameData gameData = gameDAO.getGame(command.gameID);
            AuthData user = authDAO.getAuth(command.authToken);

            String resignNotification = user.username() + " has resigned";
            ServerMessage resignMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.gameID, null, resignNotification);

            connections.broadcast(user.username(), new Notification(resignMessage.toString()));

            connections.clearGame(command.gameID);

        } catch (DataAccessException | IOException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    gameID, "Error making move", null);
            try {
                session.getRemote().sendString(new Gson().toJson(moveError));
            } catch (IOException _) {
            }
        }

    }

    private void leave(Session session, String message) throws IOException {
        LeaveCommand command = null;
        try {
            command = new Gson().fromJson(message, LeaveCommand.class);

            checkCommand(command.authToken, command.gameID);
            AuthData user = authDAO.getAuth(command.authToken);
            GameData gameData = gameDAO.getGame(command.gameID);

            String whiteUser = gameData.whiteUsername();
            String blackUser = gameData.blackUsername();

            if (user.username().equals(whiteUser)) {
                whiteUser = null;
            }
            if (user.username().equals(blackUser)) {
                blackUser = null; // making it so when we update the user is cleared out
            }

            GameData update = new GameData(gameData.gameID(), whiteUser, blackUser,
                    gameData.gameName(), gameData.game()); /* keep everything the same just replace either
                                                                the black or white user */
            gameDAO.updateGame(update);  // send the updated game to the server

            connections.remove(command.gameID);

            String leaveNotification = user.username() + " left game";  // message we want displayed
            ServerMessage leaveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.gameID, null, leaveNotification);  // creating the ServerMessage

            String leaveOutput = new Gson().toJson(leaveMessage);
            connections.broadcast(user.username(), new Notification(leaveOutput)); // broad cast that the user left

        } catch (DataAccessException | IOException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    gameID, "Error making move", null);
            try {
                session.getRemote().sendString(new Gson().toJson(moveError));
            } catch (IOException _) {
            }
        }

    }

    private void checkCommand(String command, Integer command1) throws DataAccessException {
        AuthData user = authDAO.getAuth(command);
        if (user == null) {
            throw new DataAccessException("Unauthorized");
        }
        GameData gameData = gameDAO.getGame(command1); // get the game data to manage it
        if (gameData == null) {
            throw new DataAccessException("No Game");
        }
    }

    private void makeMove(Session session, String message) {
        MakeMoveCommand command = null;

        try {
            command = new Gson().fromJson(message, MakeMoveCommand.class);
            checkCommand(command.authToken, command.gameID);
            GameData gameData = gameDAO.getGame(command.gameID);
            AuthData user = authDAO.getAuth(command.authToken);

            ChessGame game = new Gson().fromJson(gameData.game().toString(), ChessGame.class);
            game.makeMove(command.move);

            var updatedGameJson = new Gson().toJson(game);
            GameData update = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game);

            gameDAO.updateGame(update);

            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    command.gameID, null, null);

            msg.setGameString(updatedGameJson);
            connections.broadcast(user.username(), new Notification(msg.toString()));

            String notification = user.username() + " made move " + command.move;
            ServerMessage notifyMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.gameID, null, notification);

            connections.broadcast(user.username(), new Notification(notifyMessage.toString()));

        } catch (DataAccessException | InvalidMoveException | IOException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    gameID, "Error making move", null);
            try {
                session.getRemote().sendString(new Gson().toJson(moveError));
            } catch (IOException _) {

            }
        }


    }

    private void connect(Session session, String message) throws DataAccessException, IOException {
        ConnectCommand command = null;
        try {
            command = new Gson().fromJson(message, ConnectCommand.class);
            connections.add(command.gameID, session);
            GameData gameData = gameDAO.getGame(command.gameID);

            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, command.gameID,
                    null, null);

            String board = new Gson().toJson(gameData.game());
            serverMessage.setGameString(board);

            String output = new Gson().toJson(serverMessage);
            session.getRemote().sendString(output);
        } catch (DataAccessException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, gameID,
                    "Error Connecting", null );
        }
    }


}
