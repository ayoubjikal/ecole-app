package com.ecole.management.service;

import com.ecole.management.model.User;
import com.ecole.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsActive(true);
        user.setCreatedAt(new Date());

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            return findByEmail(email).orElse(null);
        }
        return null;
    }

    public void updateLastLogin(User user) {
        user.setLastLogin(new Date());
        userRepository.save(user);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}