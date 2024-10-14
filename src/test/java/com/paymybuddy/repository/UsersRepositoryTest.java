package com.paymybuddy.repository;

import com.paymybuddy.model.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test") // Utilise le profil de test avec H2
@DataJpaTest
public class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    private static final Logger logger = LogManager.getLogger(UsersRepositoryTest.class);


    @Test
    public void findByEmail_ShouldReturnUser_WhenUserExists() {
        // given

        String emailUser = "johndoe@example.com";

        // when

        Optional<Users> found = usersRepository.findByEmail(emailUser);

        // then
        assertThat(found).isPresent();
        assertEquals("JohnDoe", found.get().getUsername());
    }

    @Test
    public void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // given
        String email = "not_exist@gmail.com";

        // when
        Optional<Users> found = usersRepository.findByEmail(email);

        // then
        assertThat(found).isNotPresent();
    }

    @Test
    public void findById_ShouldReturnUser_WhenUserExists() {
        // given
        Long idUser = 1L; // Assurez-vous que cet ID correspond à un utilisateur existant


        //List<Frind> users = userRepository.findAll();
        //Long idUser = users.get(0).getId(); // Récupère l'ID du premier utilisateur


        // when
        Optional<Users> found = usersRepository.findById(idUser);

        // then
        assertThat(found).isPresent();
        assertEquals("JohnDoe", found.get().getUsername());
        //assertThat(found.get().getUsername()).isEqualTo(users.get(0).getUsername());
    }


    @Test
    public void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // given
        Long id = 999L;

        // when
        Optional<Users> found = usersRepository.findById(id);

        // then
        assertThat(found).isNotPresent();
    }


    @Test
    public void findById_ShouldReturnUser_WhenNewUser(){
        // given
        Users user = new Users();
        user.setUsername("newUser");
        user.setPassword("passWord****");
        user.setEmail("newuser@exemple.com");
        usersRepository.save(user);

        // when
        Optional<Users> found = usersRepository.findById(user.getId());

        // then
        assertThat(found).isPresent();
        assertEquals(found.get().getEmail(),user.getEmail());

    }

}