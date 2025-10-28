package nl.hva.elections.controllers;

import nl.hva.elections.dtos.LoginRequest;
import nl.hva.elections.dtos.LoginResponse;
import nl.hva.elections.models.User;
import nl.hva.elections.security.JwtTokenProvider;
import nl.hva.elections.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Authenticate the user
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            // 2. If successful, create a token
            String token = jwtTokenProvider.createToken(user);

            // 3. Return the token in the response
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (RuntimeException e) {
            // If authentication fails, return an unauthorized error
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
