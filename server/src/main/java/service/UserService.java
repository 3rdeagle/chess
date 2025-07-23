package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import dataaccess.UserDao;
import org.mindrot.jbcrypt.BCrypt;
import service.requests.*;
import service.results.LoginResult;
import service.results.RegisterResult;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDao userDao;
    private final AuthDAO authDao;
    public UserService(UserDao userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult registerUser(RegisterRequest request) throws DataAccessException {
        if (userDao.getUser(request.username()) != null) {
         throw new DataAccessException("Error: Already Taken");
        }

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData newUser = new UserData(request.username(), hashedPassword, request.email());
        userDao.createUser(newUser);

        String authToken = UUID.randomUUID().toString();
        authDao.createAuth(new AuthData(authToken, request.username()));

        return new RegisterResult(newUser.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (userDao.getUser(request.username()) == null ) {
            throw new DataAccessException("No such username");
        }

        UserData logUser = userDao.getUser(request.username());
        if (!BCrypt.checkpw(request.password(), logUser.password())) {
            throw new DataAccessException("Wrong Password");
        }

        String authToken = UUID.randomUUID().toString();
        authDao.createAuth(new AuthData(authToken, logUser.username()));

        return new LoginResult(logUser.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException{
        AuthData logoutAuth = authDao.getAuth(authToken);
        if (logoutAuth == null) {
            throw new DataAccessException("Unauthorized");
        }
        authDao.deleteAuth(logoutAuth.authToken());
    }


}


