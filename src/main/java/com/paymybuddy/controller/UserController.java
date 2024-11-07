package com.paymybuddy.controller;

import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Contrôleur pour gérer les opérations liées à l'utilisateur :
 * Créer un compte,
 * login,
 * Update du profil,
 * ajout de relations (connexion)
 */
@Controller
public class UserController {

    private final UserService userService;


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page d'inscription.
     *
     * @param model Le modèle pour la vue.
     * @return La page d'inscription.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Affichage du formulaire d'inscription.");
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Gère l'inscription de l'utilisateur.
     *
     * @param user L'utilisateur à enregistrer.
     * @return Redirige vers la page de connexion.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        logger.info("Enregistrement d'un nouvel utilisateur: {}", user.getEmail());
        userService.registerUser(user);
        return "redirect:/login";
    }

    /**
     * Affiche la page de login.
     *
     * @return La page de login.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        logger.info("Affichage du formulaire de login.");
        return "login";
    }

    /**
     * Affiche la page de profil de l'utilisateur connecté.
     *
     * @param principal L'objet Principal représentant l'utilisateur actuellement connecté.
     * @param model     Le modèle pour la vue.
     * @return La page de profil.
     */
    @GetMapping("/profile")
    public String showProfilePage(Principal principal, Model model) {
        String username = principal.getName(); // Récupérer le nom d'utilisateur depuis Principal
        logger.info("Affichage du profil pour l'utilisateur: {}", username);

        User user = userService.findByEmail(username).orElse(null);
        model.addAttribute("user", user);

        return "profile"; // Correspond au fichier profile.html
    }

    /**
     * Modifie le mot de passe de l'utilisateur (si fourni).
     *
     * @param principal   L'utilisateur actuellement connecté.
     * @param newPassword Le nouveau mot de passe, s'il est fourni.
     * @param model       Le modèle pour la vue.
     * @return Redirige vers la page du profil.
     */
    @PostMapping("/profile/update")
    public String updateProfile(Principal principal, @RequestParam(value = "newPassword", required = false) String newPassword, Model model) {

        String username = principal.getName();
        logger.info("Mise à jour du mot de passe pour l'utilisateur: {}", username);

        User user = userService.findByEmail(username).orElse(null);

        if (user != null) {
            if (newPassword != null && !newPassword.isEmpty()) {
                userService.updatePassword(user, newPassword);
                logger.info("Mot de passe mis à jour pour l'utilisateur: {}", username);
                model.addAttribute("success", "Mot de passe mis à jour avec succès.");
            }
        } else {
            logger.error("Utilisateur non trouvé pour la mise à jour du profil.");
            model.addAttribute("error", "Utilisateur non trouvé.");
        }

        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Affiche la page d'ajout de relation.
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param model     Le modèle pour la vue.
     * @return La page d'ajout de relation.
     */
    @GetMapping("/addConnection")
    public String showAddConnectionForm(Principal principal, Model model) {
        String emailUser = principal.getName();
        User user = userService.findByEmail(emailUser).orElse(null);
        logger.info("Affichage du formulaire pour ajouter une connexion.");
        model.addAttribute("user", user);

        return "addConnection"; // Correspond au fichier addConnection.html
    }

    /**
     * Gère l'ajout d'une connexion (relation).
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param email     L'adresse e-mail de l'utilisateur à ajouter.
     * @param model     Le modèle pour la vue.
     * @return Redirige vers la page d'ajout de relations.
     */
    @PostMapping("/connections/add")
    public String addConnection(Principal principal, @RequestParam("email") String email, Model model) {

        String emailUser = principal.getName();
        logger.info("Ajout d'une nouvelle connexion pour l'utilisateur: {}", emailUser);

        User user = userService.findByEmail(emailUser).orElse(null);
        User connection = userService.findByEmail(email).orElse(null);

        if (user != null && connection != null) {
            if (!user.getConnections().contains(connection)) {
                userService.addConnection(user, connection);
                logger.info("Connexion ajoutée avec succès pour l'utilisateur: {}", emailUser);
                model.addAttribute("success", "Relation ajoutée avec succès.");
            } else {
                logger.warn("La relation existe déjà pour l'utilisateur: {}", emailUser);
                model.addAttribute("error", "Cette relation existe déjà.");
            }
        } else {
            logger.warn("Impossible de trouver l'utilisateur ou la connexion: {}", email);
            model.addAttribute("error", "Utilisateur non trouvé.");
        }

        model.addAttribute("user", user);
        return "addConnection";
    }



    /**
     * Affiche la page d'ajout de relation.
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param model     Le modèle pour la vue.
     * @return La page d'ajout de relation.
     */
    @GetMapping("/connections")
    public String showConnectionsForm(Principal principal, Model model) {
        String emailUser = principal.getName();
        User user = userService.findByEmail(emailUser).orElse(null);
        logger.info("Affichage des realtions.");
        model.addAttribute("user", user);

        return "connections"; // Correspond au fichier addConnection.html
    }

}
