package dataaccess;

import model.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDao {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void clearUsers() throws DataAccessException {
        new SQLAuthDAO().clearAuth();
        var statement = "DELETE FROM users";
        executeUpdate(statement);
    }

    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, user.username(), user.password(), user.email());
        } catch (DataAccessException e) {
            throw new DataAccessException("Username AlreadyTaken", e);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var prepStatement = conn.prepareStatement(statement)) {
                prepStatement.setString(1, username);
                try (var rs = prepStatement.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read username", e);
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }


    private void executeUpdate(String statement, Object... params) throws DataAccessException {
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
                }
                prepStatement.executeUpdate();
                var rs = prepStatement.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database", e);
        }
    }


    private final String[] createStatements = {
            """ 
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(255) NOT NULL,
            `password` varchar(255) NOT NULL,
            `email` varchar(255) NOT NULL,
            PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("Current catalog: " + conn.getCatalog());
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
