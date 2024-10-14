package com.paymybuddy.service;

import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.Users;

import com.paymybuddy.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.List;

/**
 * Service pour gérer les transactions.
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UsersRepository usersRepository;

    private static final Logger logger = LogManager.getLogger(UsersRepository.class);

    /**
     * Effectue un transfert d'argent entre deux utilisateurs.
     *
     * @param sender      L'utilisateur envoyant de l'argent.
     * @param receiver    L'utilisateur recevant de l'argent.
     * @param amount      Le montant à transférer.
     * @param description La description de la transaction.
     * @return La transaction créée.
     * @throws Exception Si le solde du sender est insuffisant.
     */
    @Transactional
    public Transaction sendMoney(Users sender, Users receiver, Double amount, String description) throws Exception {

        logger.info("Tentative de transfert d'argent de {} à {} : montant = {}", sender.getEmail(), receiver.getEmail(), amount);

        // Vérifier le solde, appliquer les frais, gérer les exceptions

        // Vérifier que le sender a suffisamment de fonds
        double fee = amount * 0.005; // 0.5% de frais
        double totalAmount = amount + fee;

        if (sender.getBalance() >= totalAmount) {
            // Débiter le compte de l'expéditeur
            sender.setBalance(sender.getBalance() - totalAmount);
            // Créditer le compte du destinataire
            receiver.setBalance(receiver.getBalance() + amount);

            // Créer la transaction
            Transaction transaction = new Transaction();
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            transaction.setDescription(description);

            // Enregistrer les modifications
            usersRepository.save(sender);
            usersRepository.save(receiver);
            return transactionRepository.save(transaction);
        } else {
            logger.error("Solde insuffisant pour l'utilisateur: {}", sender.getEmail());
            throw new Exception("Solde insuffisant pour effectuer la transaction.");
        }
    }

    /**
     * Récupère les transactions liées à un utilisateur.
     *
     * @param user L'utilisateur pour lequel récupérer les transactions.
     * @return La liste des transactions de l'utilisateur.
     */
    public List<Transaction> findTransactionsForUser(Users user) {

        logger.info("Tentative de récupération des transactions de {}", user.getEmail());

        List<Transaction> sent = transactionRepository.findBySender(user);
        List<Transaction> received = transactionRepository.findByReceiver(user);
        //sent.addAll(received);
        sent.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate())); // Tri par date décroissante
        return sent;
    }

}