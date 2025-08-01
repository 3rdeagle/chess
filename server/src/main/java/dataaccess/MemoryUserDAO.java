package dataaccess;

import model.UserData;
import shared.DataAccessException;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryUserDAO implements UserDao{
    private final ConcurrentHashMap<String, UserData> users = new ConcurrentHashMap<>();

    public void clearUsers() {
        users.clear();
    }

    public void createUser(UserData user) throws DataAccessException {
        if (users.putIfAbsent(user.username(), user) != null) {
            throw new DataAccessException("Username Already Taken");
        }
    }

    public UserData getUser(String username) {
        return users.get(username);
    }



}
