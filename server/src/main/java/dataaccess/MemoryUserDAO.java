package dataaccess;

import model.UserData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryUserDAO {
    private final ConcurrentHashMap<String, UserData> map = new ConcurrentHashMap<>();

    public void clear() {
        map.clear();
    }

    //public void createUser(UserData ud) {}
}
