package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import webSocketMessages.Action;

import java.io.IOException;

public class WebSocketHandler {


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Action action = new Gson().fromJson(message, Action.class);
        switch (action.type()) {
            case ENTER -> enter(action.visitorName(), session);
            case EXIT -> exit(action.visitorName());
        }
    }
}
