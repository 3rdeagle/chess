package dataaccess;

import model.GameData;
import service.requests.CreateGameRequest;

import java.util.List;

public interface GameDAO {

    void clearGames() throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    GameData createGame(CreateGameRequest newGame) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData update) throws DataAccessException;
}
