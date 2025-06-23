package com.fintrack.fintrack_api.security;

import com.fintrack.fintrack_api.dto.request.LoginRequestDTO;
import com.fintrack.fintrack_api.dto.response.JwtResponse;
import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.service.JwtService;
import com.fintrack.fintrack_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            String jwt = jwtService.generateJwtToken(authentication);

            Users user = userService.findByEmail(request.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + request.email()));;

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    "Bearer",
                    user.getEmail(),
                    user.getRoles()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
