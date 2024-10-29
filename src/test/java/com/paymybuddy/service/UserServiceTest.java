package com.paymybuddy.service;

import com.paymybuddy.entity.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.UserServiceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test unitaire pour UsersService
 */
@ActiveProfiles("test") // Utilise le profil de test avec H2
@ExtendWith(MockitoExtension.class) // Active Mockito pour les tests
public class UserServiceTest {

    @Mock
    private UserRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    //@InjectMocks
    private UserService usersService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    private User user;

    /**
     * Initialisation des données avant chaque test
     */
    @BeforeEach
    public void setUp() {

        // Crée une instance de UsersServiceImpl en utilisant les mocks
        usersService = new UserServiceImpl(usersRepository, passwordEncoder);

        // Créez un utilisateur de test
        user = new User();
        user.setId(1L);
        user.setUsername("newUser");
        user.setPassword("passWord****");
        user.setEmail("newuser@exemple.com");
        user.setBalance(100.00);
    }

    /**
     * Nettoie les mocks après chaque test
     */
    @AfterEach
    public void tearDown() {
        reset(usersRepository, passwordEncoder);
    }

    /**
     * Test simple pour vérifier si le mock PasswordEncoder fonctionne
     */
    @Test
    public void testPasswordEncoderMock() {
        when(passwordEncoder.encode("testpassword")).thenReturn("encodedPassword");

        String encoded = passwordEncoder.encode("testpassword");

        assertEquals("encodedPassword", encoded); // Vérifie que le mot de passe est bien encodé
        verify(passwordEncoder, times(1)).encode("testpassword"); // Vérifie que l'encodage a bien été appelé
    }

    /**
     * Test pour charger un utilisateur par email (utilisateur existant)
     */
    @Test
    public void testLoadUserByUsername_UserExists() {
        when(usersRepository.findByEmail("newuser@exemple.com")).thenReturn(Optional.of(user));

        var userDetails = usersService.loadUserByUsername("newuser@exemple.com");

        assertNotNull(userDetails); // Vérifie que l'utilisateur n'est pas nul
        assertEquals("newuser@exemple.com", userDetails.getUsername());
        verify(usersRepository, times(1)).findByEmail("newuser@exemple.com"); // Vérifie que le repository est appelé une fois
    }

    /**
     * Test pour charger un utilisateur par email (utilisateur non trouvé)
     */
    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(usersRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            usersService.loadUserByUsername("unknown@example.com");
        });

        verify(usersRepository, times(1)).findByEmail("unknown@example.com"); // Vérifie que la méthode est appelée
    }

    /**
     * Test pour l'enregistrement d'un nouvel utilisateur
     */
    @Test
    public void testRegisterUser() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpassword");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usersRepository.save(any(User.class))).thenReturn(newUser);

        User registeredUser = usersService.registerUser(newUser);

        assertNotNull(registeredUser); // Vérifie que l'utilisateur est bien enregistré
        assertEquals("new@example.com", registeredUser.getEmail());
        verify(usersRepository, times(1)).save(any(User.class)); // Vérifie que l'utilisateur est sauvegardé
    }

    /**
     * Test pour trouver un utilisateur par email (utilisateur existant)
     */
    @Test
    public void testFindByEmail_UserExists() {
        String userEmail = "newuser@exemple.com";

        when(usersRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        Optional<User> foundUser = usersService.findByEmail(userEmail);

        assertTrue(foundUser.isPresent()); // Vérifie que l'utilisateur est trouvé
        assertEquals(userEmail, foundUser.get().getEmail());
        verify(usersRepository, times(1)).findByEmail(userEmail);
    }

    /**
     * Test pour trouver un utilisateur par email (utilisateur non trouvé)
     */
    @Test
    public void testFindByEmail_UserNotFound() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<User> foundUser = usersService.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent()); // Vérifie que l'utilisateur n'est pas trouvé
        verify(usersRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    /**
     * Test pour ajouter une connexion entre utilisateurs
     */
    @Test
    public void testAddConnection() {
        User connection = new User();
        connection.setEmail("friend@example.com");

        usersService.addConnection(user, connection);

        assertTrue(user.getConnections().contains(connection)); // Vérifie que la connexion a été ajoutée
        verify(usersRepository, times(1)).save(user); // Vérifie que l'utilisateur est sauvegardé avec la nouvelle connexion
    }

    /**
     * Test pour la mise à jour d'un utilisateur
     */
    @Test
    public void testUpdateUser() {
        usersService.updateUser(user);

        verify(usersRepository, times(1)).save(user); // Vérifie que l'utilisateur est sauvegardé
    }

    /**
     * Test pour la mise à jour du mot de passe d'un utilisateur
     */
    @Test
    public void testUpdatePassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        usersService.updatePassword(user, "newpassword");

        assertEquals("newEncodedPassword", user.getPassword()); // Vérifie que le mot de passe a bien été mis à jour
        verify(usersRepository, times(1)).save(user); // Vérifie que l'utilisateur est sauvegardé avec le nouveau mot de passe
    }


    /**
     * Test pour vérifier si deux utilisateurs sont connectés
     */
    @Test
    public void testIsConnection() {
        User connection = new User();
        connection.setEmail("friend@example.com");
        user.getConnections().add(connection);

        boolean isConnection = usersService.isConnection(user, connection);

        assertTrue(isConnection); // Vérifie que la connexion existe
    }

    /**
     * Test pour ajouter une connexion déjà existante
     */
    @Test
    public void testAddConnection_ConnectionAlreadyExists() {
        User connection = new User();
        connection.setId(2L);
        connection.setEmail("friend@example.com");

        user.getConnections().add(connection);

        usersService.addConnection(user, connection);

        assertEquals(1, user.getConnections().size()); // Vérifie que la connexion n'est pas ajoutée deux fois
        verify(usersRepository, never()).save(user); // Vérifie que la sauvegarde n'est pas appelée
        logger.info("Connexion déjà existante pour l'utilisateur: {}", user.getEmail());
    }
}