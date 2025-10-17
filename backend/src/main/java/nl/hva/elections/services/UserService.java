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
     * Creates and saves a new user.
     * In a real app, we would add logic here like checking for duplicate usernames
     * or encrypting the password before saving.
     * @param user The User object to save.
     * @return The saved User object, now with an ID.
     */
    public User createUser(User user) {
        // actual good logic would go here. For now, we just save it directly.
        return userRepository.save(user);
    }
}