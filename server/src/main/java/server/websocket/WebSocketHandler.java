package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.io.IOException;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        ServerMessage action = new Gson().fromJson(message, ServerMessage.class);
        switch (action.getServerMessageType()) {
            case LOAD_GAME -> loadGame(session, action);
            case ERROR -> error(session, action);
            case NOTIFICATION -> notification(session, action);

        }
    }

    public void loadGame(Session session, ServerMessage message) {
        GameData game = message.getGameData();
        connections.broadcast("", new Notification(Notification.Type.GAME_CREATED, game));
    }
}
