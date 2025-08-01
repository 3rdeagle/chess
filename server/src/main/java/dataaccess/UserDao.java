package dataaccess;

import model.UserData;
import shared.DataAccessException;

public interface UserDao {

    void clearUsers() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

}
