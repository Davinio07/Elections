package nl.hva.elections.dtos;

/**
 * A data transfer object for receiving login credentials from a user.
 */
public class LoginRequest {
    private String username;
    private String password;

    // Getters en setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}