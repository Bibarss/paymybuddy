package com.paymybuddy.repository;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Interface TransactionRepository pour accéder aux données des transactions dans la base de données.
 * Hérite de JpaRepository pour bénéficier des opérations CRUD standard.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Récupère la liste des transactions effectuées par un utilisateur donné.
     *
     * @param sender L'utilisateur qui a envoyé les transactions.
     * @return La liste des transactions envoyées par l'utilisateur.
     */
    List<Transaction> findBySender(Users sender);

    /**
     * Récupère la liste des transactions reçues par un utilisateur donné.
     *
     * @param receiver L'utilisateur qui a reçu les transactions.
     * @return La liste des transactions reçues par l'utilisateur.
     */
    List<Transaction> findByReceiver(Users receiver);
}