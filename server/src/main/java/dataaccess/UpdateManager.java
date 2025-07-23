package dataaccess;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class UpdateManager {

    public static void manageParams(PreparedStatement prepStatement, Object... params) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            if (param instanceof String p) {
                prepStatement.setString(i + 1, p);
            }
            else if (param == null) {
                prepStatement.setNull(i + 1, NULL);
            }
        }
    }
}
