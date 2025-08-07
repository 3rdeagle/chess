package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand    {
    public ChessMove move;
    public String commandType;
    public String authToken;
    public int gameID;

    public MakeMoveCommand () {
    }

    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        this.commandType = "MAKE_MOVE";
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
    }

}
