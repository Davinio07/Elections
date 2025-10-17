package nl.hva.elections.models; // Make sure this package name is correct for our project structure

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a User in the database.
 * The @Entity annotation tells Hibernate to make a table out of this class.
 */
@Entity
@Table(name = "users") // Good practice to explicitly name the table in plural.
public class User {

    /**
     * The unique ID for the user, this is the primary key.
     * @Id and @GeneratedValue are standard JPA annotations.
     * Hibernate will handle generating the ID for us automatically.
     */
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password; // TODO: We'll need to encrypt this later, for now plain text is fine.
    private String email;

    // --- Constructors, Getters, and Setters ---

    // JPA needs a default no-argument constructor, so we have to add this.
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Standard getters and setters for all fields below.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}