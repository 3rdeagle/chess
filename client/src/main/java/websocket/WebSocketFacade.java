package websocket;


import com.google.gson.Gson;
import dataaccess.GameDAO;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private Session session;
    private final NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
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
                notificationHandler.loadGame();
                break;
            case NOTIFICATION:
                notificationHandler.notification();
                break;
            case ERROR:
                notificationHandler.error();
                break;
        }
    }
}