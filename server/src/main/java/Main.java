import chess.*;

import dataaccess.*;
import server.Server;

import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

//        UserDao userDao  = new MemoryUserDAO();
//        AuthDAO authDao  = new MemoryAuthDAO();
//        GameDAO gameDao  = new MemoryGameDAO();
//
//        ClearService clearService = new ClearService(userDao, authDao, gameDao);
//        UserService  userService  = new UserService(userDao, authDao);
//        GameService gameService = new GameService(userDao, authDao, gameDao);

        new Server().run(8080);
        Spark.get("/hello", (req, res) -> "Hello Chess!");
    }
}