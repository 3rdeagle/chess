package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    public void clearGames();

    public List<GameData> listGames();
}
