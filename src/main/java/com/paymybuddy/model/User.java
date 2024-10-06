package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;

/**
 * Représente un utilisateur dans le système.
 */
@Data
@Entity
@Table(name = "User")
public class User {

    /**
     * L'ID unique de l'utilisateur, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Le nom d'utilisateur.
     */
    @Column(nullable = false)
    private String username;

    /**
     * L'adresse e-mail de l'utilisateur. Doit être unique.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Le mot de passe de l'utilisateur.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Le solde du compte de l'utilisateur.
     */
    @Column(nullable = false)
    private Double balance = 0.0;

    /**
     * Liste des transactions envoyées par l'utilisateur.
     */
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Transaction> sentTransactions = new ArrayList<>();

    /**
     * Liste des transactions reçues par l'utilisateur.
     */
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
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

    /**
     * Vérifie l'égalité entre deux objets User.
     * La comparaison est basée sur l'ID unique de l'utilisateur.
     *
     * @param o L'objet à comparer.
     * @return true si les deux objets sont égaux, sinon false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    /**
     * Génère le hash code basé sur l'ID de l'utilisateur.
     *
     * @return Le hash code de l'utilisateur.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
