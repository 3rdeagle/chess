package dataaccess;

import model.GameData;
import service.requests.CreateGameRequest;

import java.util.List;

public interface GameDAO {

    void clearGames();

    List<GameData> listGames();

    GameData createGame(CreateGameRequest newGame);

    GameData getGame(int gameID);

    void updateGame(GameData update);
}
