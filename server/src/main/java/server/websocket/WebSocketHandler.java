package server.websocket;

import chess.ChessGame;
import chess.ChessPosition;
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
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import shared.DataAccessException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
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

    private void resign(Session session, String message) throws IOException {
        ResignCommand command = null;
        try {
            command = new Gson().fromJson(message, ResignCommand.class);
            checkCommand(command.authToken, command.gameID, session);
            AuthData user = authDAO.getAuth(command.authToken);
            GameData gameData = gameDAO.getGame(command.gameID);

            if (!connections.hasGame(command.gameID)) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerMessage(ServerMessage.ServerMessageType.ERROR, command.gameID,
                                "Game over", null)));
                return;
            }


            String whiteUser = gameData.whiteUsername();
            String blackUser = gameData.blackUsername();

            if (!user.username().equals(whiteUser) && !user.username().equals(blackUser)) {
                waitTurnMessage(gameData, "Not in this game, just observe", session);
                return;
            }


            String opponent;
            if (user.username().equals(whiteUser)) {
                opponent = blackUser;
            } else {
                opponent = whiteUser;
            }

            String resignNotification = user.username() + " has resigned \n "+ opponent + " wins!!";
            ServerMessage resignMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.gameID, null, resignNotification);

            String output = new Gson().toJson(resignMessage, ServerMessage.class);

            connections.broadcast(gameData.gameID(), output);
            connections.clearGame(command.gameID);

        } catch (DataAccessException | IOException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    gameID, "Error: webhandler resign", null);
            try {
                session.getRemote().sendString(new Gson().toJson(moveError));
            } catch (IOException exception) {
                throw new IOException("behold a problem with remote in resign");
            }
        }

    }

    private void leave(Session session, String message) throws IOException, DataAccessException {
        LeaveCommand command = null;
        try {
            command = new Gson().fromJson(message, LeaveCommand.class);

            checkCommand(command.authToken, command.gameID, session);
            AuthData user = authDAO.getAuth(command.authToken);
            GameData gameData = gameDAO.getGame(command.gameID);

            if (!connections.hasGame(command.gameID)) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerMessage(ServerMessage.ServerMessageType.ERROR, command.gameID,
                                "Game over", null)));
                return;
            }

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

            connections.remove(command.gameID, session);

            String leaveNotification = user.username() + " has left the game";  // message we want displayed
            ServerMessage leaveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.gameID, null, leaveNotification);  // creating the ServerMessage

            String leaveOutputMessage = new Gson().toJson(leaveMessage);
            connections.broadcast(command.gameID, leaveOutputMessage); // broad cast that the user left

        } catch (DataAccessException | IOException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    gameID, "Error making move", null);
            try {
                session.getRemote().sendString(new Gson().toJson(moveError));
            } catch (IOException ex) {
                throw new DataAccessException("Theres a problem in leave you fool");
            }
        }
    }

    private void checkCommand(String auth, Integer gameId, Session session) throws DataAccessException, IOException {
        AuthData user = authDAO.getAuth(auth);
        if (user == null) {
//            session.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR,
//                    gameId, "Unauthorized", null)));
            throw new DataAccessException("Unauthorized");
        }
        GameData gameData = gameDAO.getGame(gameId); // get the game data to manage it
        if (gameData == null) {
//            session.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR,
//                    gameId, "No Game", null)));
            throw new DataAccessException("No Game");
        }
    }

    private void makeMove(Session session, String message) throws DataAccessException {
        MakeMoveCommand command = null;

        try {
            command = new Gson().fromJson(message, MakeMoveCommand.class);
            checkCommand(command.authToken, command.gameID, session);
            GameData gameData = gameDAO.getGame(command.gameID);
            AuthData user = authDAO.getAuth(command.authToken);

            if (!connections.hasGame(command.gameID)) {
                session.getRemote().sendString(new Gson().toJson(
                        new ServerMessage(ServerMessage.ServerMessageType.ERROR, command.gameID,
                                "Game over", null)));
                return;
            }

            ChessGame game = gameData.game().clone(); // clone the board and data
            ChessGame.TeamColor turn =  game.getTeamTurn();

            String whiteUser = gameData.whiteUsername();
            String blackUser = gameData.blackUsername();

            if (user.username().equals(whiteUser) && turn != ChessGame.TeamColor.WHITE) {
                waitTurnMessage(gameData, "Wait for your turn", session);
                return;
            }
            if (user.username().equals(blackUser) && turn != ChessGame.TeamColor.BLACK) {
                waitTurnMessage(gameData, "Wait for your turn", session);
                return;
            }
            if (!user.username().equals(whiteUser) && !user.username().equals(blackUser)) {
                waitTurnMessage(gameData, "Not in this game, just observe", session);
                return;
            }

            game.makeMove(command.move);  // make the move happen on the board
            GameData update = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game); // create the update

            gameDAO.updateGame(update); //send the update

            ServerMessage moveMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    command.gameID, null, null);

            moveMessage.setGame(game);
            String moveJson = new Gson().toJson(moveMessage);
            connections.broadcast(command.gameID, moveJson);

            String moveNotification = user.username() + " made move from " +
                    moveHelper(command.move.getStartPosition()) + " to " + moveHelper(command.move.getEndPosition());
            ServerMessage notifyMoveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.gameID, null, moveNotification);
            // create the Json string that can then be sent to broadcast
            String moveOutputMessage = new Gson().toJson(notifyMoveMessage, ServerMessage.class);

            connections.broadcastExcept(command.gameID, moveOutputMessage,session);

            ChessGame.TeamColor nextPlayer = game.getTeamTurn();

            String opponent = checkOpponent(nextPlayer, whiteUser, blackUser);

            // if the player you just made a move against is now in checkmate send that message to everyone
            if (game.isInCheckmate(nextPlayer)) {
                String checkmate = opponent + " has been put in Checkmate\n" + user.username() + "Wins by Checkmate";
                ServerMessage checkmateMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        command.gameID, null, checkmate);
                String checkmateOutputMessage = new Gson().toJson(checkmateMessage, ServerMessage.class);
                connections.broadcast(command.gameID, checkmateOutputMessage);
            }
            else if (game.isInCheck(nextPlayer)) {
                String check = opponent + " has been put in Check";
                ServerMessage checkMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        command.gameID, null, check);
                String checkOutputMessage = new Gson().toJson(checkMessage, ServerMessage.class);
                connections.broadcast(command.gameID, checkOutputMessage);
                connections.clearGame(command.gameID); // get rid of the game
            }

        } catch (DataAccessException | InvalidMoveException | IOException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    gameID, "Error Invalid move", null);
            try {
                session.getRemote().sendString(new Gson().toJson(moveError));
            } catch (IOException ex) {
                throw new DataAccessException("problem in make a move");
            }
        }
    }

    private static void waitTurnMessage(GameData gameData, String message, Session session) throws IOException {
        ServerMessage turnMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                gameData.gameID(), message, null);
        session.getRemote().sendString(new Gson().toJson(turnMessage, ServerMessage.class));
    }

    private static String checkOpponent(ChessGame.TeamColor nextPlayer, String whiteUser, String blackUser) {
        String opponent;
        if (nextPlayer == ChessGame.TeamColor.WHITE) {
            opponent = whiteUser;
        } else {
            opponent = blackUser;
        }
        return opponent;
    }

    private String moveHelper(ChessPosition position) {
        char col = (char)('a' + position.getColumn() - 1);
        int row = position.getRow();
        return "" + col + row;
    }

    private void connect(Session session, String message) throws DataAccessException, IOException {
        ConnectCommand command = null;
        try {
            command = new Gson().fromJson(message, ConnectCommand.class);
            checkCommand(command.authToken, command.gameID, session);

            connections.add(command.gameID, session); // registers session
            GameData gameData = gameDAO.getGame(command.gameID);
            ChessGame game = gameData.game();
            AuthData user = authDAO.getAuth(command.authToken);

            ServerMessage serverConnectMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, command.gameID,
                    null, null);

            serverConnectMessage.setGame(game);
            String output = new Gson().toJson(serverConnectMessage);
            session.getRemote().sendString(output);

            String color;
            if (user.username().equals(gameData.whiteUsername())) {
                color = "white";
            } else if (user.username().equals(gameData.blackUsername())) {
                color = "black";
            } else {
                color = "observer";
            }

            String connectNotification = "New user " + user.username() + " has joined the game as " + color;
            ServerMessage connectOutputMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.gameID, null, connectNotification);

            String connectJson = new Gson().toJson(connectOutputMessage, ServerMessage.class);

            connections.broadcastExcept(command.gameID, connectJson, session);

        } catch (DataAccessException e) {
            int gameID = (command != null ? command.gameID : 0);
            ServerMessage connectionError = new ServerMessage(ServerMessage.ServerMessageType.ERROR, gameID,
                    "Error Connecting", null);
            session.getRemote().sendString(new Gson().toJson(connectionError));
        }
    }


}
