package dataaccess;

import model.AuthData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.sql.*;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase(createStatements);
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
                    if (!rs.next()) {
                        throw new DataAccessException("Unauthorized");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read authtoken", e);
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth_tokens WHERE token=?";
        var rows = executeUpdate(statement, authToken);
        if (rows == 0) {
            throw new DataAccessException("No row deleted");
        }
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var id = rs.getString("token");
        var username = rs.getString("username");
        return new AuthData(id, username);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var prepStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                UpdateManager.manageParams(prepStatement, params);
                return prepStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database", e);
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


}
