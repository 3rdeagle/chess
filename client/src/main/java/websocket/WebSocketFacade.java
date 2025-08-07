package websocket;


import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {
    private Session session;
    private final NotificationHandler notificationHandler;
    private final String authToken;
    private final int gameID;

    public WebSocketFacade(String url, NotificationHandler notificationHandler, String authToken, int gameID) {
        this.notificationHandler = notificationHandler;
        this.authToken = authToken;
        this.gameID = gameID;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
//            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void onMessage(String incomeMessage) {
        ServerMessage serverMessage = new Gson().fromJson(incomeMessage, ServerMessage.class);
        // because we should be sending messages using the server message class
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                notificationHandler.loadGame(serverMessage.getGame());
                break;
            case NOTIFICATION:
                notificationHandler.notification(serverMessage.getMessage());
                break;
            case ERROR:
                notificationHandler.error(serverMessage.getErrorMessage());
                break;
        }
    }

    public void closeSession() throws IOException {
        session.close();
    }

    public void sendConnection() throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new ConnectCommand(authToken, gameID)));
    }

    public void sendResign() throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new ResignCommand(authToken, gameID)));
    }

    public void sendLeave() throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new LeaveCommand(authToken, gameID)));
    }

    public void sendMove(ChessMove move) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new MakeMoveCommand(authToken, gameID, move)));
    }
}