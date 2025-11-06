package nl.hva.elections.services;

import nl.hva.elections.dtos.LoginResponse; // <-- 1. IMPORT
import nl.hva.elections.models.User;
import nl.hva.elections.repositories.UserRepository;
import nl.hva.elections.security.JwtTokenProvider; // <-- 2. IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // <-- 3. IMPORT
import org.springframework.security.core.Authentication; // <-- 4. IMPORT
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @class UserService
 * @description Contains all the business logic for users, like creating them and finding them.
 * By implementing UserDetailsService, this service is now the official way for Spring Security
 * to get user information.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructor for the UserService.
     * We use constructor injection here. It's a fancy way of saying "Spring, please give me
     * the tools I need to do my job".
     * @param userRepository The repository to talk to the 'users' table.
     * @param passwordEncoder The tool to encrypt and check passwords.
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) { // <-- 6. UPDATE CONSTRUCTOR
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider; // <-- 7. VOEG TOE
    }

    /**
     * This is the main method that Spring Security will call.
     * When a user tries to log in, Spring will ask this method: "Hey, can you find me a user with this username?".
     * @param username The username the user is trying to log in with.
     * @return A UserDetails object (our own User class) that Spring can use.
     * @throws UsernameNotFoundException if we can't find the user in our database.
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Creates a new user.
     * It checks for duplicates and encrypts the password before saving.
     * @param user The user object with the plain-text password.
     * @return The saved user with the encrypted password.
     */
    public LoginResponse createUser(User user) {
        // Check if username is already taken
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("Username is already taken");
        });
        // Check if email is already taken
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Email is already in use");
        });

        // Encrypt the password before storing it.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // we create a auth object for the saved user
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser, null, savedUser.getAuthorities()
        );

        // use the JwtTokenProvider to create a token
        String token = jwtTokenProvider.createToken(authentication);

        // give the token back to the caller
        return new LoginResponse(token);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}