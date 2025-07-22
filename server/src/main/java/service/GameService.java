package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDao;
import model.AuthData;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import java.util.Collection;

public class GameService {

    private final AuthDAO authDao;
    private final GameDAO gameDao;
    public GameService(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        checkAuthorization(authToken);
        return gameDao.listGames();
    }

    public GameData createGames(String authToken, CreateGameRequest gameName) throws DataAccessException {
        checkAuthorization(authToken);
        return gameDao.createGame(gameName);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException{
        checkAuthorization(authToken);
        int gameId = request.gameID();
        String playerColor = request.playerColor();
        GameData game = gameDao.getGame(gameId);
        String white = game.whiteUsername();
        String black = game.blackUsername();

        if (white != null && black != null) {
            throw new DataAccessException("Full");
        }
        if (playerColor.equals("WHITE") && white != null) {
            throw new DataAccessException("already taken");
        }
        if (playerColor.equals("BLACK") && black != null) {
            throw new DataAccessException("already taken");
        }

        AuthData user = authDao.getAuth(authToken);
        String joinUsername = user.username();

        if (playerColor.equals("WHITE")) {
            white = joinUsername;
        } else {
            black = user.username();
        }
        GameData update = new GameData(game.gameID(), white, black, game.gameName(), game.game());
        gameDao.updateGame(update);
    }

    public void checkAuthorization(String authToken) throws DataAccessException{
        AuthData auth = authDao.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }
    }
}
