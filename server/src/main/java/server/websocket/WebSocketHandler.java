package server.websocket;

import chess.ChessGame;
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
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ServerMessage;

import javax.management.Notification;
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
    public void onMessage(Session session, String message) throws IOException {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        var action = jsonObject.get("commandType").getAsString();
        switch (action) {
            case "CONNECT" -> connect(session, message);
            case "MAKE_MOVE" -> handleMakeMove(session, message);
            case "LEAVE" -> handleLeave(session, message);
            case "RESIGN" -> handleResign(session, message);
        }
    }

    private void handleResign(Session session, String message) {
        var command = new Gson().fromJson(message, ResignCommand.class);
    }

    private void handleLeave(Session session, String message) {
        var command = new Gson().fromJson(message, LeaveCommand.class);
    }

    private void handleMakeMove(Session session, String message) {
        var command = new Gson().fromJson(message, MakeMoveCommand.class);
    }

    private void connect(Session session, String message) {
        var command = new Gson().fromJson(message, ConnectCommand.class);
        connections.add(command.gameID, session);

        session.getRemote().sendString(new LoadGameMessage(game));
    }


}
