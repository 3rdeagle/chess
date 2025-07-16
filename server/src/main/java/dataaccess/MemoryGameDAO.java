package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryGameDAO implements GameDAO{
    int gameID = 1;
    private final ConcurrentHashMap<Integer, GameData> games = new ConcurrentHashMap<>();

    public void clearGames() {
        games.clear();
    }

    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }
    //CreateGame
    public GameData createGame(CreateGameRequest newGame) {
        String gameName = newGame.gameName();
        var createdGame = new GameData(gameID++, null, null, gameName, new ChessGame());
        games.put(createdGame.gameID(), createdGame);
        return createdGame;
    }

    //get Game Data
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    //UpdateGame
    public void updateGame(GameData update) {
        games.put(update.gameID(), update);
    }
}
