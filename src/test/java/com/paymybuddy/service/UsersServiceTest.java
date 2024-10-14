package com.paymybuddy.service;

import com.paymybuddy.model.Users;
import com.paymybuddy.repository.UsersRepository;
import com.paymybuddy.repository.UsersRepositoryTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ActiveProfiles("test") // Utilise le profil de test avec H2
// Active Mockito et le contexte Spring si nécessaire
@ExtendWith(MockitoExtension.class) // Pour Mockito
public class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService usersService;

    private static final Logger logger = LogManager.getLogger(UsersServiceTest.class);

    private Users user;

    @BeforeEach
    public void setUp() {

        // Créez un utilisateur de test
        user = new Users();
        user.setId(1L);
        user.setUsername("newUser");
        user.setPassword("passWord****");
        user.setEmail("newuser@exemple.com");
        user.setBalance(100.00);
    }

    @AfterEach
    public void tearDown() {
        // Nettoyez les données ou réinitialisez les mocks si nécessaire
        reset(usersRepository, passwordEncoder);
    }

    @Test
    public void testPasswordEncoderMock() {
        // Test simple pour vérifier si le mock PasswordEncoder fonctionne
        when(passwordEncoder.encode("testpassword")).thenReturn("encodedPassword");

        String encoded = passwordEncoder.encode("testpassword");

        assertEquals("encodedPassword", encoded);
        verify(passwordEncoder, times(1)).encode("testpassword");
    }



    @Test
    public void testLoadUserByUsername_UserExists() {
        // Simulez la réponse du repository
        when(usersRepository.findByEmail("newuser@exemple.com")).thenReturn(Optional.of(user));

        // Appelez la méthode à tester
        var userDetails = usersService.loadUserByUsername("newuser@exemple.com");

        // Vérifiez que les détails de l'utilisateur sont corrects
        assertNotNull(userDetails);
        assertEquals("newuser@exemple.com", userDetails.getUsername());

        // Vérifiez que le repository a bien été appelé une fois
        verify(usersRepository, times(1)).findByEmail("newuser@exemple.com");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Simulez le cas où l'utilisateur n'est pas trouvé
        when(usersRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Vérifiez que l'exception UsernameNotFoundException est bien levée
        assertThrows(UsernameNotFoundException.class, () -> {
            usersService.loadUserByUsername("unknown@example.com");
        });

        // Vérifiez que le repository a bien été appelé une fois
        verify(usersRepository, times(1)).findByEmail("unknown@example.com");
    }



    @Test
    public void testRegisterUser() {

        // given
        Users newUser = new Users();
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpassword");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(newUser);

        // when
        Users registeredUser = usersService.registerUser(newUser);

        // then
        assertNotNull(registeredUser);
        assertEquals("new@example.com", registeredUser.getEmail());
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    public void testFindByEmail_UserExists() {

        String userEmail = "newuser@exemple.com";

        when(usersRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        Optional<Users> foundUser = usersService.findByEmail(userEmail);

        assertTrue(foundUser.isPresent());
        assertEquals(userEmail, foundUser.get().getEmail());
        verify(usersRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    public void testFindByEmail_UserNotFound() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<Users> foundUser = usersService.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
        verify(usersRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testAddConnection() {
        Users connection = new Users();
        connection.setEmail("friend@example.com");

        usersService.addConnection(user, connection);

        assertTrue(user.getConnections().contains(connection));
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser() {
        usersService.updateUser(user);

        verify(usersRepository, times(1)).save(user);
    }

    @Test
    public void testUpdatePassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        usersService.updatePassword(user, "newpassword");

        assertEquals("newEncodedPassword", user.getPassword());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    public void testDeposit() {
        user.setBalance(100.0);

        usersService.deposit(user, 50.0);

        assertEquals(150.0, user.getBalance());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    public void testWithdraw_Success() throws Exception {
        user.setBalance(100.0);

        usersService.withdraw(user, 50.0);

        assertEquals(50.0, user.getBalance());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    public void testWithdraw_Failure() {
        user.setBalance(30.0);

        assertThrows(Exception.class, () -> usersService.withdraw(user, 50.0));

        verify(usersRepository, never()).save(user);
    }

    @Test
    public void testIsConnection() {
        Users connection = new Users();
        connection.setEmail("friend@example.com");
        user.getConnections().add(connection);

        boolean isConnection = usersService.isConnection(user, connection);

        assertTrue(isConnection);
    }



    @Test
    public void testAddConnection_ConnectionAlreadyExists() {
        // Création d'un utilisateur déjà connecté
        Users connection = new Users();
        connection.setId(2L);
        connection.setEmail("friend@example.com");

        // Ajouter la connexion dans la liste des connexions de l'utilisateur
        user.getConnections().add(connection);

        // Appel de la méthode addConnection pour essayer d'ajouter la même connexion
        usersService.addConnection(user, connection);

        // Vérification que la connexion n'a pas été ajoutée une seconde fois
        assertEquals(1, user.getConnections().size()); // La taille doit toujours être 1

        // Vérification que la méthode save n'a pas été appelée, car la connexion existait déjà
        verify(usersRepository, never()).save(user);

        // Ajouter une vérification de log si nécessaire (facultatif)
        logger.info("Connexion déjà existante pour l'utilisateur: {}", user.getEmail());
    }



}
