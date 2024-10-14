package com.paymybuddy.config;

import com.paymybuddy.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'application.
 * Cette classe configure les filtres de sécurité, les services d'authentification, et d'autres aspects liés à la sécurité.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Active les annotations @PreAuthorize et similaires
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Configure le filtre de sécurité de Spring Security.
     *
     * @param http L'objet HttpSecurity pour configurer la sécurité des requêtes HTTP.
     * @param userDetailsService Le service de gestion des utilisateurs utilisé pour l'authentification.
     * @return Un SecurityFilterChain configuré pour sécuriser les requêtes HTTP.
     * @throws Exception En cas d'erreur de configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        logger.info("Configuration du filtre de sécurité");

        http
                .csrf(csrf -> {
                    logger.info("Désactivation de CSRF");
                    csrf.disable();
                }) // Désactiver CSRF si nécessaire
                .authorizeHttpRequests(authorize -> {
                    logger.info("Configuration des autorisations de requêtes HTTP");
                    authorize
                            .requestMatchers("/register", "/login", "/css/**", "/js/**").permitAll()
                            .anyRequest().authenticated();
                })
                .formLogin(form -> {
                    logger.info("Configuration de la page de connexion");
                    form
                            .loginPage("/login")
                            .defaultSuccessUrl("/transfer", true)
                            .permitAll();
                })
                .logout(logout -> {
                    logger.info("Configuration de la déconnexion");
                    logout
                            .logoutSuccessUrl("/login?logout")
                            .invalidateHttpSession(true) // Invalide la session
                            .deleteCookies("JSESSIONID") // Supprime les cookies de session
                            .permitAll();
                })
                .userDetailsService(userDetailsService); // Utiliser le userService injecté

        logger.info("Filtre de sécurité configuré avec succès");
        return http.build();
    }

    /**
     * Fournit un bean d'AuthenticationManager pour gérer l'authentification.
     *
     * @param authenticationConfiguration La configuration de l'authentification.
     * @return Un AuthenticationManager configuré.
     * @throws Exception En cas d'erreur de configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.info("Création du bean AuthenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Fournit un UserDetailsService à partir du UserService.
     *
     * @param userService Le service utilisateur à utiliser pour les détails d'authentification.
     * @return Un UserDetailsService basé sur le UserService fourni.
     */
    @Bean
    public UserDetailsService userDetailsService(UsersService userService) {
        logger.info("Création du bean UserDetailsService à partir de UserService");
        return userService;
    }

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