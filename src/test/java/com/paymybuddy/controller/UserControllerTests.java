package com.paymybuddy.controller;

import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe d'intégration pour tester le contrôleur UsersController.
 */

@ActiveProfiles("test") // Utilise le profil de test avec H2 pour les tests en mémoire
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService usersService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository usersRepository;

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
     * Teste l'ajout d'une nouvelle connexion (succès).
     */
    @Test
    public void testAddConnection_Success() throws Exception {
        Optional<User> user2 = usersService.findByEmail("user2@example.com");

        mockMvc.perform(post("/connections/add")
                        .param("email", user2.get().getEmail())
                        .principal(() -> "user1@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk()) // Vérifie un succès 200
                .andExpect(view().name("addConnection")) // Vérifie que la vue est bien renvoyée
                .andExpect(model().attributeExists("user"));
    }

    /**
     * Teste l'ajout d'une connexion avec un utilisateur non trouvé (échec).
     */
    @Test
    public void testAddConnection_UserNotFound() throws Exception {
        mockMvc.perform(post("/connections/add")
                        .param("email", "nonexistent@example.com")
                        .principal(() -> "user1@example.com") // Simule l'utilisateur connecté
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("addConnection"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Utilisateur à ajouter non trouvé: nonexistent@example.com"));
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
     * Teste l'affichage de la page de profil utilisateur.
     */
    @Test
    public void testShowProfilePage() throws Exception {
        mockMvc.perform(get("/profile")
                        .principal(() -> "user1@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    /**
     * Teste la mise à jour du profil utilisateur.
     */
    @Test
    public void testUpdateProfile_Success() throws Exception {
        mockMvc.perform(post("/profile/update")
                        .param("newPassword", "nouveauMotDePasse")
                        .principal(() -> "user1@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk()) // Vérifie un succès 200
                .andExpect(view().name("profile")) // Vérifie que la vue "profile" est renvoyée
                .andExpect(model().attributeExists("success")); // Vérifie que l'attribut "success" existe

        // Vérifie que le profil a bien été mis à jour
        User updatedUser = usersService.findByEmail("user1@example.com").orElse(null);
        assertNotNull(updatedUser);
        assertEquals("user1@example.com", updatedUser.getEmail());
    }

    /**
     * Teste l'affichage du formulaire d'ajout de connexion.
     */
    @Test
    public void testShowAddConnectionForm() throws Exception {
        User testUser = new User();
        testUser.setUsername("User5");
        testUser.setEmail("user5@example.com");
        testUser.setPassword("password");
        usersService.updateUser(testUser);

        mockMvc.perform(get("/addConnection")
                        .principal(() -> "user5@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk())
                .andExpect(view().name("addConnection"))
                .andExpect(model().attributeExists("user"));
    }

    /**
     * Teste l'ajout d'une connexion déjà existante.
     */
    @Test
    public void testAddConnection_AlreadyExists() throws Exception {
        User sender = new User();
        sender.setUsername("User8");
        sender.setEmail("user8@example.com");
        sender.setPassword("password");
        usersService.registerUser(sender);

        User receiver = new User();
        receiver.setUsername("User9");
        receiver.setEmail("user9@example.com");
        receiver.setPassword("password");
        usersService.registerUser(receiver);

        // Ajoute la connexion entre sender et receiver
        usersService.addConnection(sender, receiver);

        User existingConnection = usersService.findByEmail("user9@example.com").orElse(null);

        // Tente d'ajouter la même connexion à nouveau
        mockMvc.perform(post("/connections/add")
                        .param("email", existingConnection.getEmail())
                        .principal(() -> "user8@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk()) // Vérifie un succès 200
                .andExpect(view().name("addConnection")) // Vérifie que la vue "addConnection" est renvoyée
                .andExpect(model().attributeExists("error")) // Vérifie que l'attribut "error" existe
                .andExpect(model().attribute("error", "Cette relation existe déjà."));
    }

    /**
     * Teste l'affichage du formulaire des relations.
     */
    @Test
    public void testShowConnectionsForm() throws Exception {
        mockMvc.perform(get("/connections")
                        .principal(() -> "user1@example.com")) // Simule l'utilisateur connecté
                .andExpect(status().isOk())
                .andExpect(view().name("connections"))
                .andExpect(model().attributeExists("user"));
    }

}
