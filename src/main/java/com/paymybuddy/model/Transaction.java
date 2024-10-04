package com.paymybuddy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


import lombok.Data;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Double amount;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonBackReference // Indique le côté enfant de la relation avec User
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonBackReference // Indique le côté enfant de la relation avec User
    private User receiver;


    @Column(name = "date", nullable = false, updatable = false)
    private java.time.LocalDateTime date;


// Getters et Setters
}
