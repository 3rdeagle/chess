package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public void clearAuth();

    public void createAuth(AuthData authData);
}
