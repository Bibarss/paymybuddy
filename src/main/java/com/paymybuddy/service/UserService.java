package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


import lombok.Data;

import java.util.Optional;

@Data
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injectez l'encodeur de mots de passe

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Méthode pour charger l'utilisateur par email
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // Vous pouvez gérer les rôles si nécessaire
                .build();
    }


    // Méthode pour enregistrer un nouvel utilisateur
    public User registerUser(User user) {
        // Encodez le mot dae passe avant de le sauvegarder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }



    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void addConnection(User user, User connection) {
        user.getConnections().add(connection);
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}