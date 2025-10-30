package nl.hva.elections.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * @class User
 * @description Represents a User in our database.
 * We've made it implement UserDetails, so Spring Security knows how to handle it.
 * Think of it as our User class now speaking Spring's language.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String email;

    /**
     * Default constructor.
     * JPA needs this to create user objects from database data.
     */
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // --- Standard Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    // --- Methods required by UserDetails interface ---

    /**
     * Returns the authorities granted to the user.
     * We're not using roles like 'ADMIN' yet, so this returns an empty list.
     * @return A collection of authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Returns the user's password.
     * Spring Security uses this to check the credentials.
     * @return The encrypted password.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the user's username.
     * Spring Security uses this as the unique identifier for logging in.
     * @return The username.
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    // For our project, we'll just assume accounts are always active and valid.
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}