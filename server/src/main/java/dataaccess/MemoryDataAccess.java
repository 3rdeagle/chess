package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryDataAccess implements DataAccess{
    private int nextId = 1;
    private final ConcurrentHashMap<String, UserData> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, GameData> games = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AuthData> authTokens = new ConcurrentHashMap<>();

    public void clearDatabase() {
        users.clear();
        games.clear();
        authTokens.clear();
    }


}
