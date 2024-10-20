package com.paymybuddy.repository;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Users;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de test pour TransactionRepository.
 */
@DataJpaTest
@ActiveProfiles("test") // Utilise le profil de test avec H2 pour les tests en mémoire
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UsersRepository usersRepository; // Utilisation du repository Users pour créer des utilisateurs

    private Optional<Users> sender;
    private Optional<Users> receiver;

    private static final Logger logger = LogManager.getLogger(TransactionRepositoryTest.class);

    /**
     * Méthode exécutée avant chaque test pour initialiser les utilisateurs.
     */
    @BeforeEach
    void setUp() {
        // Recherche des utilisateurs par email
        sender = usersRepository.findByEmail("sender@example.com");
        receiver = usersRepository.findByEmail("receiver@example.com");
    }

    /**
     * Test pour vérifier que les transactions envoyées par un utilisateur existent.
     */
    @Test
    public void findBySender_ShouldReturnTransactions_WhenSenderExists() {
        // when : récupération des transactions envoyées par le sender
        List<Transaction> transactions = transactionRepository.findBySender(sender.get());

        // then : vérification que les transactions existent et que les informations sont correctes
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getSender().getUsername()).isEqualTo("SenderUser");
    }

    /**
     * Test pour vérifier que les transactions reçues par un utilisateur existent.
     */
    @Test
    public void findByReceiver_ShouldReturnTransactions_WhenReceiverExists() {
        // when : récupération des transactions reçues par le receiver
        List<Transaction> transactions = transactionRepository.findByReceiver(receiver.get());

        // then : vérification que les transactions existent et que les informations sont correctes
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getReceiver().getUsername()).isEqualTo("ReceiverUser");
    }

    /**
     * Test pour vérifier qu'un utilisateur sans transactions envoyées retourne une liste vide.
     */
    @Test
    public void findBySender_ShouldReturnEmptyList_WhenSenderHasNoTransactions() {
        // given : création d'un nouvel utilisateur sans transactions
        Users newSender = new Users();
        newSender.setUsername("NewSender");
        newSender.setEmail("newsender@example.com");
        newSender.setPassword("password789");
        usersRepository.save(newSender);

        // when : récupération des transactions envoyées par ce nouvel utilisateur
        List<Transaction> transactions = transactionRepository.findBySender(newSender);

        // then : vérification que la liste est vide
        assertThat(transactions).isEmpty();
    }

    /**
     * Test pour vérifier qu'un utilisateur sans transactions reçues retourne une liste vide.
     */
    @Test
    public void findByReceiver_ShouldReturnEmptyList_WhenReceiverHasNoTransactions() {
        // given : création d'un nouvel utilisateur sans transactions
        Users newReceiver = new Users();
        newReceiver.setUsername("NewReceiver");
        newReceiver.setEmail("newreceiver@example.com");
        newReceiver.setPassword("password789");
        usersRepository.save(newReceiver);

        // when : récupération des transactions reçues par ce nouvel utilisateur
        List<Transaction> transactions = transactionRepository.findByReceiver(newReceiver);

        // then : vérification que la liste est vide
        assertThat(transactions).isEmpty();
    }
}
