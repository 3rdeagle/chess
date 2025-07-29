package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import server.results.CreateGameResult;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class ServerFacade {
    private final String serverUrl;
    private String authtoken;

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

    public Collection<GameData> listGames() throws DataAccessException {
        var path = "/game";
        return this.makeRequest("GET", path, null, null);
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

    public void observeGame(int id) throws DataAccessException {
        var path = "/session";
        this.makeRequest("POST", path, null, null);
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
                http.http.setDoOutput(true);
            }
            

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw DataAccessException.fromJson(respErr);
                }
            }

            throw new DataAccessException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
