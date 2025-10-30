package nl.hva.elections.dtos;

/**
 * A DTO for sending back a JWT token after a successful login.
 */
public class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}