package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Users;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UsersRepository;
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

/**
 * Classe de test unitaire pour TransactionService
 */
@ActiveProfiles("test") // Utilise le profil de test avec H2 pour les tests en mémoire
@ExtendWith(MockitoExtension.class) // Intègre Mockito pour les tests
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

    /**
     * Initialisation des données avant chaque test
     */
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

    /**
     * Test pour une transaction réussie
     */
    @Test
    public void testSendMoney_SuccessfulTransaction() throws Exception {
        // given
        double amount = 100.0;
        String description = "Payment";
        double fee = amount * 0; // Calcul des frais de transaction
        double totalAmount = amount + fee; // Montant total déduit du solde

        // when
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Appel de la méthode de service
        Transaction result = transactionService.sendMoney(sender, receiver, amount, description);

        // then
        assertNotNull(result); // Vérifie que la transaction n'est pas nulle
        assertEquals(sender.getEmail(), result.getSender().getEmail());
        assertEquals(receiver.getEmail(), result.getReceiver().getEmail());
        assertEquals(amount, result.getAmount());
        verify(usersRepository, times(1)).save(sender); // Vérifie que le sender a été sauvegardé
        verify(usersRepository, times(1)).save(receiver); // Vérifie que le receiver a été sauvegardé
        verify(transactionRepository, times(1)).save(any(Transaction.class)); // Vérifie que la transaction a été sauvegardée

        // Vérification des soldes après la transaction
        assertEquals(1000.0 - totalAmount, sender.getBalance());
        assertEquals(500.0 + amount, receiver.getBalance());
    }

    /**
     * Test pour une transaction avec solde insuffisant
     */
    @Test
    public void testSendMoney_InsufficientBalance() {
        // given
        sender.setBalance(50.0); // Solde insuffisant pour couvrir le montant et les frais

        // when & then
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.sendMoney(sender, receiver, 100.0, "Payment");
        });

        assertEquals("Solde insuffisant pour effectuer la transaction.", exception.getMessage());
        verify(usersRepository, never()).save(sender); // Vérifie que le sender n'est pas sauvegardé
        verify(usersRepository, never()).save(receiver); // Vérifie que le receiver n'est pas sauvegardé
        verify(transactionRepository, never()).save(any(Transaction.class)); // Vérifie que la transaction n'est pas sauvegardée
    }

    /**
     * Test pour la récupération des transactions d'un utilisateur
     */
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

        // Appel de la méthode
        List<Transaction> transactions = transactionService.findTransactionsForUser(sender);

        // then
        assertEquals(1, transactions.size()); // Vérifie que le nombre de transactions est correct
        verify(transactionRepository, times(1)).findBySender(sender); // Vérifie que les transactions envoyées sont récupérées
        verify(transactionRepository, times(1)).findByReceiver(sender); // Vérifie que les transactions reçues sont récupérées
    }
}
