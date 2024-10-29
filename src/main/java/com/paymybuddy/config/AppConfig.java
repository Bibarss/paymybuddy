package com.paymybuddy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;




/**
 * Classe de configuration pour l'application.
 *
 * Cette classe contient les définitions de beans nécessaires au bon fonctionnement de l'application.
 * Utilise le framework Spring pour la gestion des configurations.
 */
@Configuration
public class AppConfig {


    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    /**
     * Fournit un encodeur de mots de passe utilisant BCrypt.
     *
     * @return Un PasswordEncoder utilisant BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Création du bean PasswordEncoder utilisant BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }
}
