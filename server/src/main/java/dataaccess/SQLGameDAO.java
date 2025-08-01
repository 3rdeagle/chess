package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import server.requests.CreateGameRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase(createStatements);
    }

    public void clearGames() throws DataAccessException {
        var statement = "DELETE FROM games";
        executeUpdate(statement);
    }

    public List<GameData> listGames() throws DataAccessException{
        List<GameData> result = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM games";
            try (var prepStatement = conn.prepareStatement(statement)) {
                try (var rs = prepStatement.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read game", e);
        }
        return result;
    }

    //CreateGame
    public GameData createGame(CreateGameRequest newGame) throws DataAccessException {
        var statement = "INSERT INTO games ( whiteUsername, blackUsername, gameName, gameJson) " +
                "VALUES ( ?, ?, ?, ?)";
        var newGameJson = new Gson().toJson(new ChessGame());
        try {
            int newGameID = executeUpdate(statement, null, null, newGame.gameName(), newGameJson);
            var newGameObject = new Gson().fromJson(newGameJson, ChessGame.class);
            return new GameData(newGameID, null, null, newGame.gameName(), newGameObject);
        } catch (DataAccessException e) {
            throw new DataAccessException("Can't create game", e);
        }
    }

    //get Game Data
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, " +
                    "gameJson FROM games WHERE gameID=?";

            try (var prepStatement = conn.prepareStatement(statement)) {
                prepStatement.setInt(1, gameID);
                try (var rs = prepStatement.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read gameID", e);
        }
        return null;
    }

    //UpdateGame
    public void updateGame(GameData update) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, gameJson=? WHERE gameID=?";
        var gameJson = new Gson().toJson(update.game());
        executeUpdate(statement, update.whiteUsername(), update.blackUsername(), update.gameName(), gameJson, update.gameID());
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJson = rs.getString("gameJson");
        ChessGame gameObject = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var prepStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        prepStatement.setString(i + 1, p);
                    }
                    else if (param == null) {
                        prepStatement.setNull(i + 1, NULL);
                    }
                    else if (param instanceof Integer p) {
                        prepStatement.setInt(i+1, p);
                    }
                }
                prepStatement.executeUpdate();
                var rs = prepStatement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database", e);
        }
    }


    private final String[] createStatements = {
            """ 
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(255) DEFAULT NULL,
            `blackUsername` varchar(255) DEFAULT NULL,
            `gameName` varchar(255) NOT NULL,
            `gameJson` TEXT NOT NULL,
             PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
