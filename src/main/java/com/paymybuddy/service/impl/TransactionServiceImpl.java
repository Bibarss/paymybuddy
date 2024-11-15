package com.paymybuddy.service.impl;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.exception.TransactionException;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.TransactionService;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implémentation du service pour gérer les transactions.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param transactionRepository Le dépôt des transactions.
     * @param userRepository        Le dépôt des utilisateurs.
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Effectue un transfert d'argent entre deux utilisateurs.
     *
     * @param sender      L'utilisateur envoyant de l'argent.
     * @param receiver    L'utilisateur recevant de l'argent.
     * @param amount      Le montant à transférer.
     * @param description La description de la transaction.
     * @return La transaction créée.
     * @throws InsufficientBalanceException Si le solde du sender est insuffisant.
     */
    @Transactional
    @Override
    public Transaction sendMoney(User sender, User receiver, BigDecimal amount, String description) throws TransactionException {

        logger.info("Tentative de transfert d'argent de {} à {} : montant = {}", sender.getEmail(), receiver.getEmail(), amount);


        // Vérifier que le montant est positif
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        if (sender.getBalance().compareTo(amount) >= 0) {
            // Débiter le compte de l'expéditeur
            sender.setBalance(sender.getBalance().subtract(amount));
            // Créditer le compte du destinataire
            receiver.setBalance(receiver.getBalance().add(amount));

            // Créer la transaction
            Transaction transaction = new Transaction();
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            transaction.setDescription(description);


            // Enregistrer les modifications
            userRepository.save(sender);
            userRepository.save(receiver);
            logger.info("Transaction réussie de {} à {}", sender.getEmail(), receiver.getEmail());
            return transactionRepository.save(transaction);
        } else {
            logger.error("Solde insuffisant pour l'utilisateur: {}", sender.getEmail());
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer la transaction.");
        }
    }

    /**
     * Récupère les transactions liées à un utilisateur.
     *
     * @param user L'utilisateur pour lequel récupérer les transactions.
     * @return La liste des transactions de l'utilisateur.
     */
    @Override
    public List<Transaction> findTransactionsForUser(User user) {

        logger.info("Récupération des transactions pour l'utilisateur: {}", user.getEmail());

        List<Transaction> transactions = transactionRepository.findBySenderOrReceiver(user, user);

        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate())); // Tri par date décroissante
        return transactions;
    }
}
