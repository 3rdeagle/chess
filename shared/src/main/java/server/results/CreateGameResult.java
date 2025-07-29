package server.results;

import service.requests.CreateGameRequest;

public record CreateGameResult(boolean success, String message, int gameID) {
}
