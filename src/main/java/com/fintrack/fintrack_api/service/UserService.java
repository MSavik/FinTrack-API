package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public Users registerUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public Optional<Users> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
