package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.sql.*;

public class SQLAuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public void clearAuth() throws DataAccessException {
        var statement = "DELETE FROM auth_tokens";
        executeUpdate(statement);
    }

    public void createAuth(AuthData auth) throws DataAccessException{
        var statement = "INSERT INTO auth_tokens (token, username) VALUES (?,?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT token, username FROM auth_tokens WHERE token= ?";
            try (var prepStatement = conn.prepareStatement(statement)) {
                prepStatement.setString(1, authToken);
                try (var rs = prepStatement.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read");
        }
        return null;
    }



    private AuthData readAuth(ResultSet rs) throws SQLException {
        var id = rs.getString("token");
        var username = rs.getString("username");
        return new AuthData(id, username);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var prepStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        prepStatement.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        prepStatement.setInt(i + 1, p);
                    }
                    else if (param == null) {
                        prepStatement.setNull(i + 1, NULL);
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
            throw new DataAccessException("Unable to update database");
        }
    }



    private final String[] createStatements = {
            """ 
            CREATE TABLE IF NOT EXISTS auth_tokens (
            `token` varchar(255) NOT NULL,
            `username` varchar(255) NOT NULL,
            FOREIGN KEY (username) REFERENCES users(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't configure table");
        }
    }
}
