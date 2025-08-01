package shared;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public static DataAccessException fromJson(InputStream respErr) {
        var map = new Gson().fromJson(new InputStreamReader(respErr), HashMap.class);
        String message = map.get("message").toString();
        return new DataAccessException(message);
    }
}
