package com.paymybuddy.service;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test unitaire pour TransactionService
 */
@ActiveProfiles("test") // Utilise le profil de test
@ExtendWith(MockitoExtension.class) // Intègre Mockito pour les tests
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Empêche le remplacement par H2
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository usersRepository;

    //@InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;
    private Transaction transaction;



    /**
     * Initialisation des données avant chaque test
     */
    @BeforeEach
    public void setUp() {

        // Crée une instance de UsersServiceImpl en utilisant les mocks
        transactionService = new TransactionServiceImpl(transactionRepository, usersRepository);

        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");
        sender.setBalance(new BigDecimal("1000.0"));

        receiver = new User();
        receiver.setId(2L);
        receiver.setEmail("receiver@example.com");
        receiver.setBalance(new BigDecimal("500.0"));

        transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(new BigDecimal("100.0"));
        transaction.setDescription("Payment");
    }

    /**
     * Test pour une transaction réussie
     */
    @Test
    public void testSendMoney_SuccessfulTransaction() throws Exception {
        // given

        BigDecimal amount = new BigDecimal("100.0");

        String description = "Payment";

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
        assertEquals(new BigDecimal("900.0"), sender.getBalance());  // 1000.0 - 100.0
        assertEquals(new BigDecimal("600.0"), receiver.getBalance()); // 500.0 + 100.0
    }

    /**
     * Test pour une transaction avec solde insuffisant
     */
    @Test
    public void testSendMoney_InsufficientBalance() {
        // given
        sender.setBalance(new BigDecimal("50.0")); // Solde insuffisant pour couvrir le montant et les frais

        // when & then
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.sendMoney(sender, receiver, new BigDecimal("100.0"), "Payment");
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
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        //List<Transaction> receivedTransactions = new ArrayList<>();
        Transaction receivedTransaction = new Transaction();
        receivedTransaction.setSender(receiver);
        receivedTransaction.setReceiver(sender);
        receivedTransaction.setAmount(new BigDecimal("50.0"));
        receivedTransaction.setDescription("Refund");
        transactionList.add(receivedTransaction);

        // when

        when(transactionRepository.findBySenderOrReceiver(receiver, receiver)).thenReturn(transactionList);

        // Appel de la méthode
        List<Transaction> transactions = transactionService.findTransactionsForUser(receiver);

        // then envoy&e 1 et reçu 1
        assertEquals(2, transactions.size()); // Vérifie que le nombre de transactions est correct
        verify(transactionRepository, times(1)).findBySenderOrReceiver(receiver, receiver); // Vérifie que les transactions récus sont récupérées

    }
}
