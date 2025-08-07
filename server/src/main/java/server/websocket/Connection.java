package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String username;
    public Session session;
    private int gameID;
    private boolean observer;

    public Connection(int gameID, Session session) {
        this.gameID = gameID;
        this.session = session;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getUsername() {
        return username;
    }

    public int getGameID() {
        return gameID;
    }

    public boolean isObserver() {
        return observer;
    }
}
