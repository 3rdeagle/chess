package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        var sessionSet = connections.get(gameID);
        if (sessionSet == null) {
            sessionSet = ConcurrentHashMap.newKeySet();
            connections.put(gameID, sessionSet);
        }
        sessionSet.add(session);
    }

    public void remove(int gameID, Session session) {
        Set<Session> sessionSet = connections.get(gameID);
        if (sessionSet != null) {
            sessionSet.remove(session);  // remove specifcally the session of that player
            if (sessionSet.isEmpty()) {
                connections.remove(gameID); // if no one if in there remove the game from connections
            }
        }
    }

    public void broadcast(int gameId, String message) throws IOException {
        Set<Session> sessions = connections.get(gameId);
        if (sessions == null) {
            return;
        }
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }

    public void broadcastExcept(int gameID, String message, Session excluded) throws IOException {
        Set<Session> sessionSet = connections.get(gameID);
        if (sessionSet == null) {
            return;
        }
        for (Session ses : sessionSet) {
            if (ses != excluded && ses.isOpen()) {
                ses.getRemote().sendString(message);
            }
        }
    }

    public void clearGame(int gameID) { // should I put a message saying something
        connections.remove(gameID);
    }

    public boolean hasGame(int gameID) {
        Set<Session> sessionSet = connections.get(gameID);
        return sessionSet != null && !sessionSet.isEmpty();
    }
}
