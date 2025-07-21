package service;

import dataaccess.DataAccessException;
import dataaccess.UserDao;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public class ClearService {
    private final UserDao userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;
    public ClearService(UserDao userDao, AuthDAO authDao, GameDAO gameDao){
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public void clear() throws DataAccessException {
        userDao.clearUsers();
        authDao.clearAuth();
        gameDao.clearGames();
    }
}
