package service;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import dataaccess.UserDao;
import service.requests.RegisterRequest;
import service.results.RegisterResult;
import java.util.UUID;




public class UserService {
    private final UserDao userDao;
    private final AuthDAO authDao;
    public UserService(UserDao userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult registerUser(RegisterRequest request) {
        if (request == null || request.username() == null || request.email() == null || request.password() == null) {
            // throw bad request some how??
        }

        // if the username is already taken
        if (userDao.getUser(request.username()) != null) {
         throw new AlreadyTakenException();
        }

        String authToken = UUID.randomUUID().toString();
        authDao.createAuth(new AuthData(authToken, request.username()));

        return new RegisterResult(request.username(), authToken);
        }
    }
