package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import service.requests.CreateGameRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryGameDAO implements GameDAO{
    int gameID = 0;
    private final ConcurrentHashMap<Integer, GameData> games = new ConcurrentHashMap<>();

    public void clearGames() {
        games.clear();
    }

    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }
    //CreateGame
    public GameData createGame(String authToken, CreateGameRequest newGame) throws DataAccessException{
        String gameName = newGame.gameName();
        var createdGame = new GameData(gameID++, null, null, gameName, new ChessGame());
        games.put(createdGame.gameID(), createdGame);
        return createdGame;
    }

    //UpdateGame

    // getGame
}
