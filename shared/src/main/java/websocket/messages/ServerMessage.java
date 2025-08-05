package websocket.messages;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    private int gameID;
    private String errorMessage;
    private String notification;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, int gameID, String errorMessage, String notification) {
        this.serverMessageType = type;
        this.gameID = gameID;
        this.errorMessage = errorMessage;
        this.notification = notification;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNotification() {
        return notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
