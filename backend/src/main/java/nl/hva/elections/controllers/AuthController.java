package nl.hva.elections.controllers;

import nl.hva.elections.dtos.LoginRequest;
import nl.hva.elections.dtos.LoginResponse;
import nl.hva.elections.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @class AuthController
 * @description This controller handles all requests related to authentication, like logging in.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructor for the AuthController.
     * @param authenticationManager The manager that will handle the login process.
     * @param jwtTokenProvider The tool to create a JWT after a successful login.
     */
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Handles the login request from the user.
     * @param loginRequest The DTO containing the username and password from the frontend form.
     * @return A ResponseEntity with a JWT token if successful, or an error if it fails.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // This is where the magic happens. We ask the AuthenticationManager to check the user.
            // It will automatically use our UserService and PasswordEncoder to do this.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // If we get here, the password was correct!
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Now, we create the token for the successfully logged-in user.
            String token = jwtTokenProvider.createToken(authentication);

            // Send the token back to the frontend.
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            // If anything goes wrong (e.g., wrong password), the manager throws an exception.
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}