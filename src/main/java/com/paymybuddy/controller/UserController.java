package com.paymybuddy.controller;

import com.paymybuddy.entity.User;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.service.UserService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Contrôleur pour gérer les opérations liées à l'utilisateur :
 * création de compte, login, mise à jour du profil, ajout de connexions.
 */
@Controller
public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Constructeur avec injection du service utilisateur.
     *
     * @param userService Le service utilisateur.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Affiche la page d'inscription.
     *
     * @param model Le modèle pour la vue.
     * @return Le nom de la vue pour l'inscription.
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
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        logger.info("Tentative d'enregistrement d'un nouvel utilisateur: {}", user.getEmail());

        // Vérifier les erreurs de validation
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de l'inscription: {}", result.getAllErrors());
            return "register";
        }

        // Vérifier si l'email est déjà utilisé
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("L'email {} est déjà utilisé.", user.getEmail());
            model.addAttribute("error", "Cet email est déjà utilisé.");
            return "register";
        }

        userService.registerUser(user);
        logger.info("Utilisateur enregistré avec succès: {}", user.getEmail());
        return "redirect:/login";
    }


    /**
     * Affiche la page de connexion.
     *
     * @return Le nom de la vue pour la connexion.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        logger.info("Affichage du formulaire de connexion.");
        return "login";
    }

    /**
     * Affiche la page de profil de l'utilisateur connecté.
     *
     * @param principal L'objet Principal représentant l'utilisateur actuellement connecté.
     * @param model     Le modèle pour la vue.
     * @return Le nom de la vue pour le profil.
     */
    @GetMapping("/profile")
    public String showProfilePage(Principal principal, Model model) {
        String username = principal.getName(); // Récupérer le nom d'utilisateur depuis Principal
        logger.info("Affichage du profil pour l'utilisateur: {}", username);

        User user = userService.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + username));

        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Modifie le mot de passe de l'utilisateur (si fourni).
     *
     * @param principal   L'utilisateur actuellement connecté.
     * @param newPassword Le nouveau mot de passe, s'il est fourni.
     * @param model       Le modèle pour la vue.
     * @return Le nom de la vue pour le profil.
     */
    @PostMapping("/profile/update")
    public String updateProfile(Principal principal, @RequestParam(value = "newPassword", required = false) String newPassword, Model model) {

        String username = principal.getName();
        logger.info("Mise à jour du mot de passe pour l'utilisateur: {}", username);

        User user = userService.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + username));

        if (newPassword != null && !newPassword.isEmpty()) {
            userService.updatePassword(user, newPassword);
            logger.info("Mot de passe mis à jour pour l'utilisateur: {}", username);
            model.addAttribute("success", "Mot de passe mis à jour avec succès.");
        }

        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Affiche la page d'ajout de connexion.
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param model     Le modèle pour la vue.
     * @return Le nom de la vue pour l'ajout de connexion.
     */
    @GetMapping("/addConnection")
    public String showAddConnectionForm(Principal principal, Model model) {
        String emailUser = principal.getName();
        User user = userService.findByEmail(emailUser)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + emailUser));
        logger.info("Affichage du formulaire pour ajouter une connexion.");
        model.addAttribute("user", user);

        return "addConnection";
    }

    /**
     * Gère l'ajout d'une connexion (relation).
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param email     L'adresse e-mail de l'utilisateur à ajouter.
     * @param model     Le modèle pour la vue.
     * @return Le nom de la vue pour l'ajout de connexion.
     */
    @PostMapping("/connections/add")
    public String addConnection(Principal principal, @RequestParam("email") String email, Model model) {

        String emailUser = principal.getName();
        logger.info("Ajout d'une nouvelle connexion pour l'utilisateur: {}", emailUser);

        try {
            User user = userService.findByEmail(emailUser)
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + emailUser));
            User connection = userService.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur à ajouter non trouvé: " + email));

            if (!user.getConnections().contains(connection)) {
                userService.addConnection(user, connection);
                logger.info("Connexion ajoutée avec succès pour l'utilisateur: {}", emailUser);
                model.addAttribute("success", "Relation ajoutée avec succès.");
            } else {
                logger.warn("La relation existe déjà pour l'utilisateur: {}", emailUser);
                model.addAttribute("error", "Cette relation existe déjà.");
            }

            model.addAttribute("user", user);
            return "addConnection";

        } catch (UserNotFoundException ex) {
            logger.warn("Erreur lors de l'ajout de connexion: {}", ex.getMessage());
            model.addAttribute("error", ex.getMessage());
            return "addConnection";
        }
    }

    /**
     * Affiche la liste des connexions de l'utilisateur.
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param model     Le modèle pour la vue.
     * @return Le nom de la vue pour les connexions.
     */
    @GetMapping("/connections")
    public String showConnectionsForm(Principal principal, Model model) {
        String emailUser = principal.getName();
        User user = userService.findByEmail(emailUser)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + emailUser));
        logger.info("Affichage des relations.");
        model.addAttribute("user", user);

        return "connections";
    }
}
