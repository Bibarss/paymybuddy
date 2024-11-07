package com.paymybuddy.repository;

import com.paymybuddy.entity.User;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Classe de test pour UsersRepository.
 */
@ActiveProfiles("test") // Utilise le profil de test avec H2 pour les tests en mémoire
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Empêche le remplacement par H2
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryTests.class);

    /**
     * Test pour vérifier qu'un utilisateur est trouvé par son email lorsqu'il existe.
     */
    @Test
    public void findByEmail_ShouldReturnUser_WhenUserExists() {
        // given : l'email d'un utilisateur existant
        String emailUser = "johndoe@example.com";

        // when : recherche de l'utilisateur par email
        Optional<User> found = userRepository.findByEmail(emailUser);

        // then : vérification que l'utilisateur est trouvé et que les informations sont correctes
        assertThat(found).isPresent();
        assertEquals("JohnDoe", found.get().getUsername());
    }

    /**
     * Test pour vérifier qu'un utilisateur introuvable par email retourne une valeur vide.
     */
    @Test
    public void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // given : un email qui n'existe pas
        String email = "not_exist@gmail.com";

        // when : recherche d'un utilisateur par cet email
        Optional<User> found = userRepository.findByEmail(email);

        // then : vérification que l'utilisateur n'est pas trouvé
        assertThat(found).isNotPresent();
    }

    /**
     * Test pour vérifier qu'un utilisateur est trouvé par son ID lorsqu'il existe.
     */
    @Test
    public void findById_ShouldReturnUser_WhenUserExists() {
        // given : un ID correspondant à un utilisateur existant
        Long idUser = 1L; // Assurez-vous que cet ID correspond à un utilisateur existant

        // when : recherche de l'utilisateur par son ID
        Optional<User> found = userRepository.findById(idUser);

        // then : vérification que l'utilisateur est trouvé et que les informations sont correctes
        assertThat(found).isPresent();
        assertEquals("JohnDoe", found.get().getUsername());
    }

    /**
     * Test pour vérifier qu'un utilisateur introuvable par ID retourne une valeur vide.
     */
    @Test
    public void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // given : un ID qui n'existe pas
        Long id = 999L;

        // when : recherche d'un utilisateur par cet ID
        Optional<User> found = userRepository.findById(id);

        // then : vérification que l'utilisateur n'est pas trouvé
        assertThat(found).isNotPresent();
    }

    /**
     * Test pour vérifier qu'un nouvel utilisateur est trouvé après son enregistrement.
     */
    //@Rollback(false)
    @Test
    public void findById_ShouldReturnUser_WhenNewUser() {
        // given : création d'un nouvel utilisateur
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("passWord****");
        user.setEmail("newuser@exemple.com");
        userRepository.save(user); // Sauvegarde du nouvel utilisateur

        // when : recherche de cet utilisateur par son ID
        Optional<User> found = userRepository.findById(user.getId());

        // then : vérification que l'utilisateur est trouvé et que les informations sont correctes
        assertThat(found).isPresent();
        assertEquals(found.get().getEmail(), user.getEmail());
    }
}
