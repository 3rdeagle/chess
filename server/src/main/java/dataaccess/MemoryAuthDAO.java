package dataaccess;

import model.AuthData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryAuthDAO {
    private final ConcurrentHashMap<String, AuthData> authTokens = new ConcurrentHashMap<>();

    public void clearAuth() {
        authTokens.clear();
    }

    public void createAuth(AuthData auth) {
        authTokens.put(auth.authToken(), auth);
    }
}
