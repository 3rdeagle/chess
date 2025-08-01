package dataaccess;

import model.AuthData;
import shared.DataAccessException;

public interface AuthDAO {

    void clearAuth() throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
