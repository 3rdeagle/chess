package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryGameDAO implements GameDAO{
    private final ConcurrentHashMap<String, GameData> games = new ConcurrentHashMap<>();

    public void clearGames() {
        games.clear();
    }

    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }
    //CreateGame

    //UpdateGame

    // getGame
}
