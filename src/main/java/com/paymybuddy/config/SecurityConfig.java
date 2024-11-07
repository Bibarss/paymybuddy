package com.paymybuddy.config;

import com.paymybuddy.service.impl.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'application.
 * Cette classe configure les filtres de sécurité, les services d'authentification, et d'autres aspects liés à la sécurité.
 */

@Configuration  // Indique que cette classe contient des configurations Spring.
@EnableWebSecurity  // Active la sécurité web de Spring Security.
@EnableMethodSecurity // Active les annotations @PreAuthorize et similaires
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Configure le filtre de sécurité de Spring Security.
     *
     * @param http L'objet HttpSecurity pour configurer la sécurité des requêtes HTTP.
     * @return Un SecurityFilterChain configuré pour sécuriser les requêtes HTTP.
     * @throws Exception En cas d'erreur de configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuration du filtre de sécurité");

        http
                .csrf(csrf -> {
                    logger.info("Désactivation de CSRF");
                    csrf.disable();
                }) // Désactiver CSRF si nécessaire
                .authorizeHttpRequests(authorize -> {   //Définit les règles d'autorisation pour les requêtes HTTP
                    logger.info("Configuration des autorisations de requêtes HTTP");
                    authorize
                            .requestMatchers("/register", "/login", "/css/**", "/js/**").permitAll() // et les ressources statiques sont accessibles à tous
                            .anyRequest().authenticated(); // Toutes les autres requêtes (anyRequest()) nécessitent une authentification
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
                });

        logger.info("Filtre de sécurité configuré avec succès");
        return http.build();
    }




    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return auth.build();
    }

}