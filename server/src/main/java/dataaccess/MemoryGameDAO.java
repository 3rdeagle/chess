package dataaccess;

import model.GameData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryGameDAO {
    private final ConcurrentHashMap<String, GameData> games = new ConcurrentHashMap<>();

    public void clearGames() {
        games.clear();
    }
}
