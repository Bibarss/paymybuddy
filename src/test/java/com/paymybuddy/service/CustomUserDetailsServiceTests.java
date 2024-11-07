package com.paymybuddy.service;

import com.paymybuddy.entity.User;

import com.paymybuddy.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

/**
 * Classe de test unitaire pour UsersService
 */
@ActiveProfiles("temvn st") // Utilise le profil de test avec H2
@ExtendWith(MockitoExtension.class) // Active Mockito pour les tests
public class CustomUserDetailsServiceTests {

   @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsServiceTests.class);

    private User user;

    /**
     * Initialisation des données avant chaque test
     */
    @BeforeEach
    public void setUp() {


        // Crée une instance de UsersServiceImpl en utilisant les mocks
        //userService = new UserServiceImpl(userRepository, passwordEncoder);

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
        reset(userService, passwordEncoder);
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
        when(userService.findByEmail("newuser@exemple.com")).thenReturn(Optional.of(user));

        var userDetails = customUserDetailsService.loadUserByUsername("newuser@exemple.com");

        assertNotNull(userDetails); // Vérifie que l'utilisateur n'est pas nul
        assertEquals("newuser@exemple.com", userDetails.getUsername());
        verify(userService, times(1)).findByEmail("newuser@exemple.com"); // Vérifie que le repository est appelé une fois
    }

    /**
     * Test pour charger un utilisateur par email (utilisateur non trouvé)
     */
    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userService.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("unknown@example.com");
        });

        verify(userService, times(1)).findByEmail("unknown@example.com"); // Vérifie que la méthode est appelée
    }

}