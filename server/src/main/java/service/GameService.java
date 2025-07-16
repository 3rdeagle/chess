package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDao;

import java.util.Collection;

public class GameService {
    private final UserDao userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;
    public GameService(UserDao userDao, AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
        this.userDao = userDao;
    }

    public Collection<ChessGame> listGames(String authToken) throws DataAccessException {
        
    }
}
