package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
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


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test") // Utilise le profil de test avec H2
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

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void testShowHomePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user", "transactions"));
    }

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

    @Test
    @WithMockUser(username = "user1@example.com")
    public void testSendMoney_Success() throws Exception {

        //Optional<Users> user1 = usersService.findByEmail("user1@example.com");
        Optional<Users> user2 = usersService.findByEmail("user2@example.com");

        mockMvc.perform(post("/transactions/send")
                        .param("connectionEmail", user2.get().getEmail())
                        .param("amount", "100")
                        .param("description", "Payment for service"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void testAddConnection_Success() throws Exception {

        Optional<Users> user2 = usersService.findByEmail("user2@example.com");

        mockMvc.perform(post("/connections/add")
                        .param("email", user2.get().getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void testAddConnection_UserNotFound() throws Exception {
        mockMvc.perform(post("/connections/add")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().is3xxRedirection()) // Vérifie si la réponse est une redirection
                .andExpect(redirectedUrl("/home"))  ;   // Vérifie que l'URL de redirection est "/home"
                //.andExpect(model().attributeExists("error")) // Vérifie que le modèle contient bien "error"
                //.andExpect(model().attribute("error", "Utilisateur non trouvé.")); // Vérifie la valeur exacte de l'erreur
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

/* A SUPPRIMER car il renvoie vers profile
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testAddConnection() throws Exception {
        mockMvc.perform(post("/addConnection")
                        .param("email", "newfriend@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }
*/
    /*
    @Test
    @WithMockUser(username = "user1@example.com")
    public void testShowConnections() throws Exception {


        Users user1 = usersService.findByEmail("user1@example.com").get();
        Users friend = usersService.findByEmail("user3@example.com").get();

        //friend.setEmail("friend@example.com");
        user1.getConnections().add(friend);


        mockMvc.perform(get("/connections"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("user", user1))
                .andExpect(model().attribute("connections", user1.getConnections()));
    }

*/
/*

    @Test
    @WithMockUser(username = "user1@example.com")
    public void testShowProfilePage() throws Exception {

        Optional<Users> user1 = usersService.findByEmail("user1@example.com");

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", user1.get()));
    }

 */





}
