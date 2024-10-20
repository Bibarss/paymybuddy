package com.paymybuddy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Représente une transaction entre deux utilisateurs.
 */
@Data
@Entity
@Table(name = "transaction")
public class Transaction {

    /**
     * L'ID unique de la transaction, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * La description de la transaction.
     */
    private String description;

    /**
     * Le montant de la transaction.
     */
    private Double amount;

    /**
     * L'utilisateur qui envoie la transaction.
     * Relation Many-to-One avec l'entité User.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;

    /**
     * L'utilisateur qui reçoit la transaction.
     * Relation Many-to-One avec l'entité User.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Users receiver;

    /**
     * La date et l'heure de la transaction. Initialisée à la date et l'heure actuelles.
     * Ne peut pas être modifiée une fois créée.
     */
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date = LocalDateTime.now();
}