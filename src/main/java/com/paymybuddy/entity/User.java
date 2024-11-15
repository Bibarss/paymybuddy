package com.paymybuddy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

/**
 * Représente un utilisateur dans le système.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /**
     * L'ID unique de l'utilisateur, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le nom d'utilisateur.
     */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    @Column(nullable = false)
    private String username;

    /**
     * L'adresse e-mail de l'utilisateur. Doit être unique.
     */
    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit être valide.")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Le mot de passe de l'utilisateur.
     */
    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 3, message = "Le mot de passe doit contenir au moins 3 caractères.")
    private String password;
    /**
     * Le solde du compte de l'utilisateur.
     */
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Liste des transactions envoyées par l'utilisateur.
     */
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    //@OneToMany(fetch = FetchType.EAGER, mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Transaction> sentTransactions = new ArrayList<>();

    /**
     * Liste des transactions reçues par l'utilisateur.
     */
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    //@OneToMany(fetch = FetchType.EAGER, mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Transaction> receivedTransactions = new ArrayList<>();

    /**
     * Liste des connexions (amis) de l'utilisateur.
     * Représente une relation Many-to-Many avec la table User.
     */
    @ManyToMany
    @JoinTable(
            name = "user_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private List<User> connections = new ArrayList<>();

}