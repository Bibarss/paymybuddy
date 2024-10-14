package com.paymybuddy.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import com.paymybuddy.model.Users;

/**
 * Interface UserRepository pour accéder aux données des utilisateurs dans la base de données.
 * Hérite de JpaRepository pour bénéficier des opérations CRUD standard.
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    /**
     * Recherche un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur s'il existe, sinon Optional vide.
     */
    Optional<Users> findByEmail(String email);

    /**
     * Recherche un utilisateur par son ID.
     *
     * @param id L'ID de l'utilisateur.
     * @return Un objet Optional contenant l'utilisateur s'il existe, sinon Optional vide.
     */
    Optional<Users> findById(Long id);

    /**
     * Récupère la liste des tous les utilisateurs.
     *
     * @return La liste des tous les utilisateurs.
     */
    List<Users> findAll();


    /**
     * Récupère l'utilisateur ainsi que ses connexions.
     *
     * @return La liste des toutes les connections d'un utilisateur et l'utilisateur
     */
    /*
    @EntityGraph(attributePaths = "connections")
    Optional<Users> findByEmailWithConnections(@Param("email") String email);

     */
}
