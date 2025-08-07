package websocket;

import chess.ChessGame;
import websocket.messages.Notification;

public interface NotificationHandler {

    void loadGame(ChessGame gameState);
    void notification(String message);
    void error(String errMessage);
}
