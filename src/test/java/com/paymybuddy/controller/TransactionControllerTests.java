package com.paymybuddy.controller;

import com.paymybuddy.entity.Users;
import com.paymybuddy.repository.UsersRepository;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe d'intégration pour tester le contrôleur TransactionController.
 */
@ActiveProfiles("test") // Utilise le profil de test avec H2 pour les tests en mémoire
@SpringBootTest
public class TransactionControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsersService usersService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UsersRepository usersRepository;

    private MockMvc mockMvc;

    /**
     * Initialise MockMvc avant chaque test.
     */
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Teste l'envoi d'argent entre utilisateurs (succès).
     */
    @Test
    public void testSendMoney_Success() throws Exception {
        Optional<Users> user2 = usersService.findByEmail("user2@example.com");

        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", user2.get().getEmail())
                        .param("amount", "100")
                        .param("description", "Payment for service")
                        .principal(() -> "user1@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk()) // Vérifie un succès 200
                .andExpect(view().name("transfer")) // Vérifie que la vue "addConnection" est renvoyée
                .andExpect(model().attributeExists("success")); // Vérifie que le message de succès est dans les attributs model
    }

    /**
     * Teste l'affichage de la page de transfert d'argent.
     */
    @Test
    public void testShowTransferPage() throws Exception {
        Users testUser = new Users();
        testUser.setUsername("User4");
        testUser.setEmail("user4@example.com");
        testUser.setPassword("password");
        usersService.updateUser(testUser);

        mockMvc.perform(get("/transfer")
                        .principal(() -> "user4@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("user", "transactions"));
    }

    /**
     * Teste l'envoi d'argent avec une exception (montant supérieur au solde).
     */
    @Test
    public void testSendMoney_ExceptionHandling() throws Exception {
        // Prépare les données de test
        Users sender = new Users();
        sender.setUsername("User6");
        sender.setEmail("user6@example.com");
        sender.setPassword("password");
        usersService.registerUser(sender);

        Users receiver = new Users();
        receiver.setUsername("User7");
        receiver.setEmail("user7@example.com");
        receiver.setPassword("password");
        usersService.registerUser(receiver);

        // Ajoute une connexion entre sender et receiver
        usersService.addConnection(sender, receiver);

        sender = usersService.findByEmail("user6@example.com").orElse(null);
        receiver = usersService.findByEmail("user7@example.com").orElse(null);

        // Simule une exception avec un montant supérieur au solde
        sender.setBalance(50.0);
        usersService.updateUser(sender);

        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", receiver.getEmail())
                        .param("amount", "100.0")
                        .param("description", "Test d'exception")
                        .principal(() -> "user6@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk()) // Vérifie un succès 200
                .andExpect(view().name("transfer")) // Vérifie que la vue "addConnection" est renvoyée
                .andExpect(model().attributeExists("error")) // Vérifie que l'attribut "error" existe
                .andExpect(model().attribute("error", "Erreur lors de la transaction"));
    }
}
