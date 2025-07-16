package dataaccess;

import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;

import java.util.List;

public interface GameDAO {

    public void clearGames();

    public List<GameData> listGames();

    public GameData createGame(CreateGameRequest newGame);

    public GameData getGame(int gameID);

    public void updateGame(GameData update);
}
