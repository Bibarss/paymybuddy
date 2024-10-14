package com.paymybuddy.repository;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.Users;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Utilise le profil de test avec H2
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UsersRepository usersRepository; // Assurez-vous d'avoir un repository pour Users

    private  Optional<Users> sender;
    private  Optional<Users> receiver;

    private static final Logger logger = LogManager.getLogger(TransactionRepositoryTest.class);

    @BeforeEach
    void setUp() {
        // Créez des utilisateurs pour les tests


       sender = usersRepository.findByEmail("sender@example.com");
       receiver = usersRepository.findByEmail("receiver@example.com");

        //logger.info("sender {} ", sender.get().getId());

    }

    @Test
    public void findBySender_ShouldReturnTransactions_WhenSenderExists() {
        // when
        List<Transaction> transactions = transactionRepository.findBySender(sender.get());

        // then
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getSender().getUsername()).isEqualTo("SenderUser");
    }

    @Test
    public void findByReceiver_ShouldReturnTransactions_WhenReceiverExists() {
        // when
        List<Transaction> transactions = transactionRepository.findByReceiver(receiver.get());

        // then
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getReceiver().getUsername()).isEqualTo("ReceiverUser");
    }

    @Test
    public void findBySender_ShouldReturnEmptyList_WhenSenderHasNoTransactions() {
        // given
        Users newSender = new Users();
        newSender.setUsername("NewSender");
        newSender.setEmail("newsender@example.com");
        newSender.setPassword("password789");
        usersRepository.save(newSender);

        // when
        List<Transaction> transactions = transactionRepository.findBySender(newSender);

        // then
        assertThat(transactions).isEmpty();
    }

    @Test
    public void findByReceiver_ShouldReturnEmptyList_WhenReceiverHasNoTransactions() {
        // given
        Users newReceiver = new Users();
        newReceiver.setUsername("NewReceiver");
        newReceiver.setEmail("newreceiver@example.com");
        newReceiver.setPassword("password789");
        usersRepository.save(newReceiver);

        // when
        List<Transaction> transactions = transactionRepository.findByReceiver(newReceiver);

        // then
        assertThat(transactions).isEmpty();
    }
}
