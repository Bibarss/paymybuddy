package com.paymybuddy.service;

import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction sendMoney(User sender, User receiver, Double amount, String description) {
        // Vérifier le solde, appliquer les frais, gérer les exceptions
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findTransactionsForUser(User user) {
        return null;
    }

// Autres méthodes nécessaires
}
