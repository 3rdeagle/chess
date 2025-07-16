package dataaccess;

import model.GameData;
import service.requests.CreateGameRequest;

import java.util.List;

public interface GameDAO {

    public void clearGames();

    public List<GameData> listGames();

    public GameData createGame(String authToken, CreateGameRequest newGame) throws DataAccessException;
}
