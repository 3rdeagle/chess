package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void clearAuth();

    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);
}
