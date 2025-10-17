package nl.hva.elections.services;

import nl.hva.elections.models.User;
import nl.hva.elections.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing users.
 * This class contains the business logic for user-related operations.
 * It uses the UserRepository to interact with the database.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * We use constructor injection to get the UserRepository.
     * Spring will automatically provide an instance of our repository here.
     * @param userRepository The repository for user data.
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets all users from the database.
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Creates and saves a new user after validating the data.
     * It checks if the username or email is already in use.
     * @param user The User object to save.
     * @return The saved User object, now with an ID.
     * @throws IllegalStateException if the username or email is already taken.
     */
    public User createUser(User user) {
        // 1. Controleer of de gebruikersnaam al in gebruik is.
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("Gebruikersnaam is al in gebruik");
        });

        // 2. Controleer of het e-mailadres al in gebruik is.
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("E-mailadres is al in gebruik");
        });

        // 3. Als alles oké is, sla de nieuwe gebruiker op.
        return userRepository.save(user);
    }
}