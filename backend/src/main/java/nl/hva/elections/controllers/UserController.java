package nl.hva.elections.controllers;

import nl.hva.elections.models.User;
import nl.hva.elections.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API Controller for managing users.
 * This class handles all incoming web requests related to users.
 * It's the entry point for our user management API.
 */
@RestController
@RequestMapping("/api/users") // All endpoints in this controller will start with /api/users
public class UserController {

    private final UserService userService;

    // We inject the UserService to use its methods.
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to get all users.
     * Handles HTTP GET requests to /api/users.
     * @return A list of all users in the database.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Endpoint to create a new user.
     * Handles HTTP POST requests to /api/users.
     * The @RequestBody annotation tells Spring to take the JSON from the request
     * and turn it into a User object for us.
     * @param user The user data sent in the request body.
     * @return The newly created user, including their generated ID.
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}