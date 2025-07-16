package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public void clearAuth();

    public void createAuth(AuthData authData);

    public AuthData getAuth(String authToken);

    public void deleteAuth(String authToken);
}
