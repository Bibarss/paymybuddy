package com.paymybuddy.controller;

import com.paymybuddy.model.Users;
import com.paymybuddy.repository.UsersRepository;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
 * Classe d'intégration pour tester le contrôleur UsersController.
 */
@ActiveProfiles("test") // Utilise le profil de test avec H2 pour les tests en mémoire
@SpringBootTest
public class UsersControllerIntegrationTest {

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
     * Teste l'enregistrement d'un nouvel utilisateur.
     */
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testRegisterUser() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "newuser")
                        .param("email", "newuser@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Teste l'envoi d'argent entre utilisateurs (succès).
     */
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testSendMoney_Success() throws Exception {
        Optional<Users> user2 = usersService.findByEmail("user2@example.com");

        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", user2.get().getEmail())
                        .param("amount", "100")
                        .param("description", "Payment for service"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"));
    }

    /**
     * Teste l'ajout d'une nouvelle connexion (succès).
     */
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testAddConnection_Success() throws Exception {
        Optional<Users> user2 = usersService.findByEmail("user2@example.com");

        mockMvc.perform(post("/connections/add")
                        .param("email", user2.get().getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addConnection"));
    }

    /**
     * Teste l'ajout d'une connexion avec un utilisateur non trouvé (échec).
     */
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testAddConnection_UserNotFound() throws Exception {
        mockMvc.perform(post("/connections/add")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().is3xxRedirection()) // Vérifie si la réponse est une redirection
                .andExpect(redirectedUrl("/addConnection"));
    }

    /**
     * Teste l'affichage du formulaire d'enregistrement.
     */
    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    /**
     * Teste l'affichage du formulaire de connexion.
     */
    @Test
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    /**
     * Teste l'affichage de la page de transfert d'argent.
     */
    @Test
    @WithMockUser(username = "user4@example.com")
    public void testShowTransferPage() throws Exception {
        Users testUser = new Users();
        testUser.setUsername("User4");
        testUser.setEmail("user4@example.com");
        testUser.setPassword("password");
        usersService.updateUser(testUser);

        mockMvc.perform(get("/transfer"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("users", "transactions"));
    }

    /**
     * Teste l'affichage de la page de profil utilisateur.
     */
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testShowProfilePage() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("users"));
    }

    /**
     * Teste la mise à jour du profil utilisateur.
     */
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testUpdateProfile_Success() throws Exception {
        mockMvc.perform(post("/profile/update")
                        .param("username", "nouveauNom")
                        .param("email", "nouveauemail@example.com")
                        .param("newPassword", "nouveauMotDePasse"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("success"));

        // Vérifie que le profil a bien été mis à jour
        Users updatedUser = usersService.findByEmail("nouveauemail@example.com").orElse(null);
        assertNotNull(updatedUser);
        assertEquals("nouveauNom", updatedUser.getUsername());
    }

    /**
     * Teste l'affichage du formulaire d'ajout de connexion.
     */
    @Test
    @WithMockUser(username = "user5@example.com")
    public void testShowAddConnectionForm() throws Exception {
        Users testUser = new Users();
        testUser.setUsername("User5");
        testUser.setEmail("user5@example.com");
        testUser.setPassword("password");
        usersService.updateUser(testUser);

        mockMvc.perform(get("/addConnection"))
                .andExpect(status().isOk())
                .andExpect(view().name("addConnection"))
                .andExpect(model().attributeExists("users"));
    }

    /**
     * Teste l'envoi d'argent avec une exception (montant supérieur au solde).
     */
    @Test
    @WithMockUser(username = "user6@example.com")
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
                        .param("description", "Test d'exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(flash().attributeExists("error"));
    }

    /**
     * Teste l'ajout d'une connexion déjà existante.
     */
    @Test
    @WithMockUser(username = "user8@example.com")
    public void testAddConnection_AlreadyExists() throws Exception {
        Users sender = new Users();
        sender.setUsername("User8");
        sender.setEmail("user8@example.com");
        sender.setPassword("password");
        usersService.registerUser(sender);

        Users receiver = new Users();
        receiver.setUsername("User9");
        receiver.setEmail("user9@example.com");
        receiver.setPassword("password");
        usersService.registerUser(receiver);

        // Ajoute la connexion entre sender et receiver
        usersService.addConnection(sender, receiver);

        Users existingConnection = usersService.findByEmail("user9@example.com").orElse(null);

        // Tente d'ajouter la même connexion à nouveau
        mockMvc.perform(post("/connections/add")
                        .param("email", existingConnection.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addConnection"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Cette relation existe déjà."));
    }
}
