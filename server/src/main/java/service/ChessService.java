package service;

import model.UserData;

public class ChessService {

    private final DataAccess dataAccess;
    public ChessService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public UserData addUser(UserData user) {

        return dataAccess.addUser(user);
    }

    public void clear() {
        dataAccess.clearDatabase();
    }
}
