package websocket.messages;

import com.google.gson.Gson;

public record Notification(String message) {
    public enum Type {
        ARRIVAL,
        NOISE,
        DEPARTURE
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
