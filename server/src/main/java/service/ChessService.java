package service;

import dataaccess.DataAccess;

public class ChessService {

    private final DataAccess dataAccess;
    public ChessService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clearDatabase();
    }
}
