package dataaccess;

import model.UserData;

public interface UserDao {

    void clearUsers();

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username);

}
