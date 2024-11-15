package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.exception.TransactionException;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    public Transaction sendMoney(User sender, User receiver, BigDecimal amount, String description) throws TransactionException;

    public List<Transaction> findTransactionsForUser(User user);


}
