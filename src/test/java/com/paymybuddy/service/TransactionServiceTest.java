package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.Users;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test") // Utilise le profil de test avec H2
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Users sender;
    private Users receiver;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        sender = new Users();
        sender.setId(1L);
        sender.setEmail("sender@example.com");
        sender.setBalance(1000.0);

        receiver = new Users();
        receiver.setId(2L);
        receiver.setEmail("receiver@example.com");
        receiver.setBalance(500.0);

        transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(100.0);
        transaction.setDescription("Payment");
    }

    @Test
    public void testSendMoney_SuccessfulTransaction() throws Exception {
        // given
        double amount = 100.0;
        String description = "Payment";
        double fee = amount * 0.005;
        double totalAmount = amount + fee;

        // when
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Call the method
        Transaction result = transactionService.sendMoney(sender, receiver, amount, description);

        // then
        assertNotNull(result);
        assertEquals(sender.getEmail(), result.getSender().getEmail());
        assertEquals(receiver.getEmail(), result.getReceiver().getEmail());
        assertEquals(amount, result.getAmount());
        verify(usersRepository, times(1)).save(sender);
        verify(usersRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

        // Check balances after transaction
        assertEquals(1000.0 - totalAmount, sender.getBalance());
        assertEquals(500.0 + amount, receiver.getBalance());
    }

    @Test
    public void testSendMoney_InsufficientBalance() {
        // given
        sender.setBalance(50.0); // Not enough to cover the amount + fee

        // when & then
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.sendMoney(sender, receiver, 100.0, "Payment");
        });

        assertEquals("Solde insuffisant pour effectuer la transaction.", exception.getMessage());
        verify(usersRepository, never()).save(sender);
        verify(usersRepository, never()).save(receiver);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testFindTransactionsForUser() {
        // given
        List<Transaction> sentTransactions = new ArrayList<>();
        sentTransactions.add(transaction);

        List<Transaction> receivedTransactions = new ArrayList<>();
        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setSender(receiver);
        receivedTransaction.setReceiver(sender);
        receivedTransaction.setAmount(50.0);
        receivedTransaction.setDescription("Refund");
        receivedTransactions.add(receivedTransaction);

        // when
        when(transactionRepository.findBySender(sender)).thenReturn(sentTransactions);
        when(transactionRepository.findByReceiver(sender)).thenReturn(receivedTransactions);

        // Call the method
        List<Transaction> transactions = transactionService.findTransactionsForUser(sender);

        // then
        assertEquals(2, transactions.size());
        verify(transactionRepository, times(1)).findBySender(sender);
        verify(transactionRepository, times(1)).findByReceiver(sender);
    }
}