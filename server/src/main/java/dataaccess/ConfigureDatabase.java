package dataaccess;

import java.sql.SQLException;

public class ConfigureDatabase  {

    public static void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
//            try (var prep = conn.prepareStatement("DROP TABLE IF EXISTS games")) {
//                prep.executeUpdate();
//            }
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
