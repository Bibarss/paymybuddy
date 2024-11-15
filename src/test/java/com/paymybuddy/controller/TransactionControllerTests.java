package com.paymybuddy.controller;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe d'intégration pour tester le contrôleur TransactionController.
 */
@ActiveProfiles("test") // Utilise le profil de test avec H2 pour les tests en mémoire
@SpringBootTest
@Transactional
public class TransactionControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    private MockMvc mockMvc;

    /**
     * Initialise MockMvc avec le support de Spring Security avant chaque test.
     */
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()) // Nécessaire si vous utilisez Spring Security
                .build();
    }

    /**
     * Teste l'envoi d'argent entre utilisateurs (succès).
     */
    @Test
    @WithMockUser(username = "user11@example.com")
    public void testSendMoney_Success() throws Exception {
        // Prépare les données de test pour l'expéditeur
        User sender = new User();
        sender.setUsername("User11");
        sender.setEmail("user11@example.com");
        sender.setPassword("password");
        sender.setBalance(new BigDecimal("200.0")); // Solde suffisant
        userService.registerUser(sender);

        // Prépare les données de test pour le destinataire
        User receiver = new User();
        receiver.setUsername("User22");
        receiver.setEmail("user22@example.com");
        receiver.setPassword("password");
        receiver.setBalance(new BigDecimal("100.0"));
        userService.registerUser(receiver);

        // Ajoute une connexion entre sender et receiver
        userService.addConnection(sender, receiver);

        // Exécute la requête POST pour envoyer de l'argent
        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", receiver.getEmail())
                        .param("amount", "100.0")
                        .param("description", "Payment for service"))
                //.with(csrf())) // CSRF est désactivé
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("success"))
                .andExpect(model().attribute("success", "Transaction réussie."))
                .andExpect(model().attributeExists("user", "transactions", "formattedBalance"));
    }


    /**
     * Teste l'envoi d'argent avec un solde insuffisant.
     */
    @Test
    @WithMockUser(username = "user3@example.com")
    public void testSendMoney_InsufficientBalance() throws Exception {
        // Prépare les données de test
        User sender = userService.findByEmail("user3@example.com").orElse(null);

        assert sender != null;
        sender.setBalance(new BigDecimal("100.0")); // Solde insuffisant
        userService.updateUser(sender);

        User receiver = new User();
        receiver.setUsername("User4");
        receiver.setEmail("user4@example.com");
        receiver.setPassword("password");
        userService.registerUser(receiver);

        // Ajoute une connexion entre sender et receiver
        userService.addConnection(sender, receiver);

        // Exécute la requête POST pour envoyer de l'argent
        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", receiver.getEmail())
                        .param("amount", "300.00")
                        .param("description", "Test Insufficient Balance"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Solde insuffisant pour effectuer la transaction."));
    }

    /**
     * Teste l'envoi d'argent à une connexion non autorisée.
     */
    @Test
    @WithMockUser(username = "user5@example.com")
    public void testSendMoney_UnauthorizedConnection() throws Exception {
        // Prépare les données de test
        User sender = new User();
        sender.setUsername("User5");
        sender.setEmail("user5@example.com");
        sender.setPassword("password");
        sender.setBalance(new BigDecimal("200.0"));
        userService.registerUser(sender);

        User receiver = new User();
        receiver.setUsername("User6");
        receiver.setEmail("user6@example.com");
        receiver.setPassword("password");
        userService.registerUser(receiver);

        // Note : Pas de connexion ajoutée entre sender et receiver

        // Exécute la requête POST pour envoyer de l'argent
        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", receiver.getEmail())
                        .param("amount", "50.00")
                        .param("description", "Test Unauthorized Connection"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Transaction non autorisée."));
    }

    /**
     * Teste l'envoi d'argent avec un montant invalide.
     */
    @Test
    @WithMockUser(username = "user7@example.com")
    public void testSendMoney_InvalidAmount() throws Exception {
        // Prépare les données de test
        User sender = new User();
        sender.setUsername("User7");
        sender.setEmail("user7@example.com");
        sender.setPassword("password");
        sender.setBalance(new BigDecimal("200.0"));
        userService.registerUser(sender);

        User receiver = new User();
        receiver.setUsername("User8");
        receiver.setEmail("user8@example.com");
        receiver.setPassword("password");
        userService.registerUser(receiver);

        // Ajoute une connexion entre sender et receiver
        userService.addConnection(sender, receiver);

        // Exécute la requête POST pour envoyer de l'argent avec un montant négatif
        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", receiver.getEmail())
                        .param("amount", "-50.00")
                        .param("description", "Test Invalid Amount"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Le montant doit être supérieur à zéro."));
    }

    /**
     * Teste l'affichage de la page de transfert.
     */
    @Test
    @WithMockUser(username = "user9@example.com")
    public void testShowTransferPage() throws Exception {
        // Prépare les données de test
        User testUser = new User();
        testUser.setUsername("User9");
        testUser.setEmail("user9@example.com");
        testUser.setPassword("password");
        userService.registerUser(testUser);

        // Exécute la requête GET pour afficher la page de transfert
        mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("user", "transactions"));
    }


    /**
     * Nouveau test : Vérifie que les transactions sont correctement formatées en utilisant sendMoney.
     */
    /**
     * Nouveau test : Vérifie que les transactions sont correctement formatées en utilisant sendMoney.
     */
    /**
     * Nouveau test : Vérifie que les transactions sont correctement formatées en utilisant sendMoney.
     */
    @Test
    @WithMockUser(username = "user101@example.com")
    public void testShowTransferPage_FormattedTransactionsUsingSendMoney() throws Exception {
        // Prépare les données de test pour l'expéditeur
        User sender = new User();
        sender.setUsername("User101");
        sender.setEmail("user101@example.com");
        sender.setPassword("password");
        sender.setBalance(new BigDecimal("500.00")); // Solde suffisant
        userService.registerUser(sender);

        // Prépare les données de test pour le destinataire
        User receiver = new User();
        receiver.setUsername("User201");
        receiver.setEmail("user201@example.com");
        receiver.setPassword("password");
        receiver.setBalance(new BigDecimal("100.00"));
        userService.registerUser(receiver);

        // Ajoute une connexion entre sender et receiver
        userService.addConnection(sender, receiver);

        // Exécute la requête POST pour envoyer de l'argent
        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", receiver.getEmail())
                        .param("amount", "100.0")
                        .param("description", "Test Transaction"))
                //.with(csrf())) // CSRF est désactivé
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("success"))
                .andExpect(model().attribute("success", "Transaction réussie."))
                .andExpect(model().attributeExists("user", "transactions", "formattedBalance"));

        // Exécute la requête GET pour afficher la page de transfert
        MvcResult result = mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("user", "transactions"))
                .andReturn();

        // Récupère le ModelAndView pour accéder aux attributs du modèle
        ModelAndView mav = result.getModelAndView();
        assertNotNull(mav, "ModelAndView ne doit pas être nul");

        // Récupère les transactions du modèle
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> transactions = (List<Map<String, Object>>) mav.getModel().get("transactions");
        assertNotNull(transactions, "La liste des transactions ne doit pas être nulle");
        assertFalse(transactions.isEmpty(), "La liste des transactions ne doit pas être vide");

        // Récupère la dernière transaction ajoutée via sendMoney
        Map<String, Object> latestTransactionMap = transactions.get(transactions.size() - 1);
        assertTrue(latestTransactionMap.containsKey("transaction"), "La transaction doit contenir la clé 'transaction'");
        assertTrue(latestTransactionMap.containsKey("formattedAmount"), "La transaction doit contenir la clé 'formattedAmount'");

        Transaction latestTransaction = (Transaction) latestTransactionMap.get("transaction");
        String formattedAmount = (String) latestTransactionMap.get("formattedAmount");
        assertEquals(new BigDecimal("100.0"), latestTransaction.getAmount(), "Le montant de la transaction doit être correct");

        // Vérifie que le montant est correctement formaté
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String expectedFormattedAmount = decimalFormat.format(latestTransaction.getAmount());
        assertEquals(expectedFormattedAmount, formattedAmount, "Le montant formaté doit être correct");
    }
}