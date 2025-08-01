package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import server.results.*;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;
    private String authtoken;
    private final Gson gson = new Gson();

    public ServerFacade(String url) {
        serverUrl = url;
        this.authtoken = null;
    }

    public void clearDatabase() throws DataAccessException {
        String path = "/db";
        makeRequest("DELETE", path, null, null);
    }

    public RegisterResult registerUser(RegisterRequest request) throws DataAccessException {
        var path = "/user";
        RegisterResult result = makeRequest("POST", path, request, RegisterResult.class);
        this.authtoken = result.authToken();
        return result;
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        var path = "/session";
        LoginResult result = makeRequest("POST", path, request, LoginResult.class);
        this.authtoken = result.authToken();
        return result;
    }

    public void logout() throws DataAccessException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
        this.authtoken = null; // delete the auth token off of the client
    }

    public List<GameData> listGames() throws DataAccessException {
        var path = "/game";
        ListGamesResult result = this.makeRequest("GET", path, null, ListGamesResult.class);
        assert result != null;
        return result.getGames();
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        var path = "/game";
        CreateGameResult result = this.makeRequest("POST", path, request, CreateGameResult.class);
        return result;
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        var path = "/game";
        this.makeRequest("PUT", path, request, null);
    }

    public void observeGame() throws DataAccessException { // not sure how to do this?? some how just show a game
        var path = "/session";
        this.makeRequest("POST", path, null, Void.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass)
            throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (authtoken != null) { // if the authtoken isn't null then we need to send in header Authorization
                http.setRequestProperty("Authorization", authtoken);
            }

            if (request != null) {
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
                String reqData = gson.toJson(request);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }

            http.connect();
            int status = http.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new DataAccessException("Error:" + status);
            } else {

                try (InputStream inputStream = http.getInputStream())   {
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    if (responseClass != null) {
                        return gson.fromJson(reader, responseClass);
                    }
                }
            }
        } catch (DataAccessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return null;
    }

}
