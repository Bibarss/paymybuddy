package com.paymybuddy.service.impl;

import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.UserService;

import lombok.Data;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Service pour gérer les opérations liées à l'utilisateur.
 */


@Data
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }




    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur s'il existe, sinon Optional vide.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Recherche un utilisateur par son ID.
     *
     * @param id L'ID de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur s'il existe, sinon Optional vide.
     */
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Récupère la liste des tous les utilisateurs.
     *
     * @return La liste des tous les utilisateurs.
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Recherche un utilisateur par son username
     *
     * @return La liste des toutes les connections d'un utilisateur et l'utilisateur
     */
    @Override
    public Optional<User> loadByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    /**
     * Charge un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Les détails de l'utilisateur.
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(new ArrayList<>()) // Aucune autorité
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }


    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param user L'utilisateur à enregistrer.
     * @return L'utilisateur enregistré.
     */
    @Override
    public User registerUser(User user) {
        // Encodez le mot dae passe avant de le sauvegarder
        logger.info("Enregistrement d'un nouvel utilisateur: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setBalance(100.0); // Par exemple, crédit initial
        User registeredUser = userRepository.save(user);
        logger.info("Utilisateur enregistré avec succès: {}", registeredUser.getEmail());
        return registeredUser;
    }



    /**
     * Met à jour les informations de l'utilisateur.
     *
     * @param user L'utilisateur à mettre à jour.
     */
    @Override
    public void updateUser(User user) {
        logger.info("Mise à jour des informations de l'utilisateur: {}", user.getEmail());
        userRepository.save(user);
        logger.info("Informations de l'utilisateur mises à jour avec succès.");
    }

    /**
     * Ajoute une connexion (relation) à un utilisateur.
     *
     * @param user       L'utilisateur ajoutant une connexion.
     * @param connection L'utilisateur à ajouter comme connexion.
     */
    @Override
    public void addConnection(User user, User connection) {
        logger.info("Ajout d'une connexion pour l'utilisateur: {}", user.getEmail());
        if (!user.getConnections().contains(connection)) {
            user.getConnections().add(connection);
            updateUser(user);
            logger.info("Connexion ajoutée avec succès: {}", connection.getEmail());
        } else {
            logger.warn("Connexion déjà existante pour l'utilisateur: {}", user.getEmail());
        }
    }



    /**
     * Met à jour le mot de passe de l'utilisateur.
     *
     * @param user        L'utilisateur dont le mot de passe doit être mis à jour.
     * @param newPassword Le nouveau mot de passe.
     */
    @Override
    public void updatePassword(User user, String newPassword) {
        logger.info("Mise à jour du mot de passe pour l'utilisateur: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(newPassword));
        updateUser(user);
        logger.info("Mot de passe mis à jour avec succès.");
    }


    /**
     * Vérifie si un utilisateur est une connexion.
     *
     * @param user       L'utilisateur à vérifier.
     * @param connection L'utilisateur à vérifier comme connexion.
     * @return true si l'utilisateur est une connexion, sinon false.
     */
    @Override
    public boolean isConnection(User user, User connection) {
        logger.info("Vérification de la connexion entre l'utilisateur: {} et: {}", user.getEmail(), connection.getEmail());
        return user.getConnections().contains(connection);
    }



}
