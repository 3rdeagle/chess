package dataaccess;

import model.UserData;

public interface UserDao {

    void clearUsers() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

}
