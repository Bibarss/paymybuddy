package com.paymybuddy.repository;

import com.paymybuddy.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface UserRepository pour accéder aux données des utilisateurs dans la base de données.
 * Hérite de JpaRepository pour bénéficier des opérations CRUD standard.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur s'il existe, sinon Optional vide.
     */
    Optional<User> findByEmail(String email);

    /**
     * Recherche un utilisateur par son username
     *
     * @return La liste des toutes les connections d'un utilisateur et l'utilisateur
     */
    Optional<User> findByUsername(String username);




}
