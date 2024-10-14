package com.paymybuddy.service;

import com.paymybuddy.model.Users;
import com.paymybuddy.repository.UsersRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;

import java.util.Optional;

/**
 * Service pour gérer les opérations liées à l'utilisateur.
 */
@Data
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LogManager.getLogger(UsersService.class);

    public UsersService(UsersRepository userRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    /**
     * Charge un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Les détails de l'utilisateur.
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Chargement de l'utilisateur avec l'e-mail: {}", email);
        Users user = findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // Vous pouvez gérer les rôles si nécessaire
                .build();
    }



    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param user L'utilisateur à enregistrer.
     * @return L'utilisateur enregistré.
     */
    public Users registerUser(Users user) {
        // Encodez le mot dae passe avant de le sauvegarder
        logger.info("Enregistrement d'un nouvel utilisateur: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setBalance(100.0); // Par exemple, crédit initial
        Users registeredUser = usersRepository.save(user);
        logger.info("Utilisateur enregistré avec succès: {}", registeredUser.getEmail());
        return registeredUser;
    }



    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur, s'il existe.
     */
    public Optional<Users> findByEmail(String email) {
        logger.info("Recherche de l'utilisateur avec l'e-mail: {}", email);
        return usersRepository.findByEmail(email);
    }


    /**
     * Met à jour les informations de l'utilisateur.
     *
     * @param user L'utilisateur à mettre à jour.
     */
    public void updateUser(Users user) {
        logger.info("Mise à jour des informations de l'utilisateur: {}", user.getEmail());
        usersRepository.save(user);
        logger.info("Informations de l'utilisateur mises à jour avec succès.");
    }

    /**
     * Ajoute une connexion (relation) à un utilisateur.
     *
     * @param user       L'utilisateur ajoutant une connexion.
     * @param connection L'utilisateur à ajouter comme connexion.
     */
    public void addConnection(Users user, Users connection) {
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
    public void updatePassword(Users user, String newPassword) {
        logger.info("Mise à jour du mot de passe pour l'utilisateur: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(newPassword));
        updateUser(user);
        logger.info("Mot de passe mis à jour avec succès.");
    }

    /**
     * Recharge le compte de l'utilisateur.
     *
     * @param user   L'utilisateur dont le compte doit être rechargé.
     * @param amount Le montant à ajouter.
     */
    public void deposit(Users user, Double amount) {
        logger.info("Rechargement du compte pour l'utilisateur: {} avec le montant: {}", user.getEmail(), amount);
        user.setBalance(user.getBalance() + amount);
        updateUser(user);
        logger.info("Rechargement réussi. Nouveau solde: {}", user.getBalance());
    }

    /**
     * Retire de l'argent du compte de l'utilisateur.
     *
     * @param user   L'utilisateur effectuant le retrait.
     * @param amount Le montant à retirer.
     * @throws Exception Si le solde est insuffisant pour effectuer le retrait.
     */
    public void withdraw(Users user, Double amount) throws Exception {
        logger.info("Tentative de retrait pour l'utilisateur: {} d'un montant de: {}", user.getEmail(), amount);
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            updateUser(user);
            logger.info("Retrait réussi. Nouveau solde: {}", user.getBalance());
        } else {
            logger.error("Échec du retrait. Solde insuffisant pour l'utilisateur: {}", user.getEmail());
            throw new Exception("Solde insuffisant pour effectuer le retrait.");
        }
    }

    /**
     * Vérifie si un utilisateur est une connexion.
     *
     * @param user       L'utilisateur à vérifier.
     * @param connection L'utilisateur à vérifier comme connexion.
     * @return true si l'utilisateur est une connexion, sinon false.
     */
    public boolean isConnection(Users user, Users connection) {
        logger.info("Vérification de la connexion entre l'utilisateur: {} et: {}", user.getEmail(), connection.getEmail());
        return user.getConnections().contains(connection);
    }

    /**
     * Recherche un utilisateur et ses connexions par son adresse e-mail.
     *
     * @param email  L'utilisateur à vérifier.
     * @return utilisateur et ses connexions
     */
    /*
    public Optional<Users> findByEmailWithConnections(String email) {
        return usersRepository.findByEmailWithConnections(email);
    }
*/

}