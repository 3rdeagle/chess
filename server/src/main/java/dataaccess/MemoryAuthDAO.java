package dataaccess;

import model.AuthData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final ConcurrentHashMap<String, AuthData> authTokens = new ConcurrentHashMap<>();

    public void clearAuth() {
        authTokens.clear();
    }

    public void createAuth(AuthData auth) {
        authTokens.put(auth.authToken(), auth);
    }

    //deleteAuth
    /* use the Authtoken in the map
    remove the authoken
     */

    //getAuth
    // I imagine its similar to getUser


}
