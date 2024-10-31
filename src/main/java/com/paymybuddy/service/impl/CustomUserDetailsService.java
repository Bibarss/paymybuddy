package com.paymybuddy.service.impl;

import com.paymybuddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
 * Classe de configuration pour l'application.
 *
 * Cette classe contient les définitions de beans nécessaires au bon fonctionnement de l'application.
 * Utilise le framework Spring pour la gestion des configurations.
 */
@Service
public class CustomUserDetailsService  implements UserDetailsService {


    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService .class);

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }


    /**
     * Charge un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @return Les détails de l'utilisateur.
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return userService.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(new ArrayList<>()) // Aucune autorité
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }



}
