package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;

import java.util.List;

public interface TransactionService {

    public Transaction sendMoney(User sender, User receiver, Double amount, String description) throws Exception;

    public List<Transaction> findTransactionsForUser(User user);


}
