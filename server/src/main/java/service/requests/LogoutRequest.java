package service.requests;

import dataaccess.AuthDAO;

public record LogoutRequest(String authToken) {
}
