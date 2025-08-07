package websocket;


import com.google.gson.Gson;
import dataaccess.GameDAO;
import websocket.commands.ConnectCommand;
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

    public void sendUserCommand (Object command) {
        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendConnection() {
        session.getBasicRemote().sendText(new Gson().toJson(new ConnectCommandValue(authToken, gameID)));
    }
}