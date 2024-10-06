package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.Data;

import java.util.List;

/**
 * Contrôleur pour gérer les opérations liées à l'utilisateur.
 */
@Data
@Controller
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;

    private static final Logger logger = LogManager.getLogger(UserController.class);

    public UserController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
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
     * Affiche la page de connexion.
     *
     * @return La page de connexion.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        logger.info("Affichage du formulaire de connexion.");
        return "login";
    }

    /**
     * Affiche la page d'accueil.
     *
     * @param currentUser L'utilisateur actuellement connecté.
     * @param model       Le modèle pour la vue.
     * @return La page d'accueil.
     */
    @GetMapping("/home")
    public String showHomePage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        logger.info("Affichage de la page d'accueil pour l'utilisateur: {}", currentUser.getUsername());
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        List<Transaction> transactions = transactionService.findTransactionsForUser(user);
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "home";
    }

    /**
     * Affiche la page de profil.
     *
     * @param currentUser L'utilisateur actuellement connecté.
     * @param model       Le modèle pour la vue.
     * @return La page de profil.
     */
    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        logger.info("Affichage du profil pour l'utilisateur: {}", currentUser.getUsername());
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Affiche la liste des connexions (amis) de l'utilisateur.
     *
     * @param currentUser L'utilisateur actuellement connecté.
     * @param model       Le modèle pour la vue.
     * @return La page d'accueil contenant la liste des connexions.
     */
    @GetMapping("/connections")
    public String showConnections(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        logger.info("Affichage des connexions pour l'utilisateur: {}", currentUser.getUsername());
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("connections", user.getConnections());
        return "home";
    }

    /**
     * Gère l'ajout d'une connexion (relation).
     *
     * @param currentUser L'utilisateur actuellement connecté.
     * @param email       L'adresse e-mail de l'utilisateur à ajouter.
     * @param model       Le modèle pour la vue.
     * @return Redirige vers la page d'accueil.
     */
    @PostMapping("/connections/add")
    public String addConnection(@AuthenticationPrincipal UserDetails currentUser, @RequestParam("email") String email, Model model) {
        logger.info("Ajout d'une nouvelle connexion pour l'utilisateur: {}", currentUser.getUsername());
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        User connection = userService.findByEmail(email).orElse(null);
        if (connection != null) {
            userService.addConnection(user, connection);
            logger.info("Connexion ajoutée avec succès pour l'utilisateur: {}", currentUser.getUsername());
        } else {
            model.addAttribute("error", "Utilisateur non trouvé.");
            logger.warn("Utilisateur non trouvé pour l'ajout de connexion: {}", email);
        }
        return "redirect:/home";
    }

    /**
     * Affiche la page d'ajout de relation.
     *
     * @param model Le modèle pour la vue.
     * @return La page d'ajout de relation.
     */
    @GetMapping("/addConnection")
    public String showAddConnectionForm(Model model) {
        logger.info("Affichage du formulaire pour ajouter une connexion.");
        return "addConnection";
    }

    /**
     * Gère l'ajout de relation.
     *
     * @param email L'adresse e-mail de l'utilisateur à ajouter.
     * @return Redirige vers la page de profil après l'ajout.
     */
    @PostMapping("/addConnection")
    public String addConnection(@RequestParam String email) {
        logger.info("Ajout d'une connexion avec l'email: {}", email);
        // Logique pour ajouter la relation...
        return "redirect:/profile";
    }

    /**
     * Modifie le profil de l'utilisateur (y compris le mot de passe).
     *
     * @param currentUser L'utilisateur actuellement connecté.
     * @param updatedUser Les informations mises à jour de l'utilisateur.
     * @param newPassword Le nouveau mot de passe, s'il est fourni.
     * @return Redirige vers la page d'accueil.
     */
    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails currentUser,
                                @ModelAttribute("user") User updatedUser,
                                @RequestParam(value = "newPassword", required = false) String newPassword) {
        logger.info("Mise à jour du profil pour l'utilisateur: {}", currentUser.getUsername());
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        if (user != null) {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            if (newPassword != null && !newPassword.isEmpty()) {
                userService.updatePassword(user, newPassword);
                logger.info("Mot de passe mis à jour pour l'utilisateur: {}", currentUser.getUsername());
            }
            userService.updateUser(user);
            logger.info("Profil mis à jour pour l'utilisateur: {}", currentUser.getUsername());
        } else {
            logger.error("Utilisateur non trouvé pour la mise à jour du profil.");
        }
        return "redirect:/home";
    }

    /**
     * Gère l'envoi d'argent à une connexion.
     *
     * @param currentUser    L'utilisateur actuellement connecté.
     * @param connectionEmail L'e-mail de la connexion à laquelle envoyer de l'argent.
     * @param amount         Le montant à envoyer.
     * @param description    La description de la transaction.
     * @param model          Le modèle pour la vue.
     * @return Redirige vers la page d'accueil.
     */
    @PostMapping("/transactions/send")
    public String sendMoney(@AuthenticationPrincipal UserDetails currentUser,
                            @RequestParam("connectionEmail") String connectionEmail,
                            @RequestParam("amount") Double amount,
                            @RequestParam("description") String description,
                            Model model) {
        logger.info("Envoi d'argent de {} à {}", currentUser.getUsername(), connectionEmail);
        User sender = userService.findByEmail(currentUser.getUsername()).orElse(null);
        User receiver = userService.findByEmail(connectionEmail).orElse(null);

        if (sender != null && receiver != null && sender.getConnections().contains(receiver)) {
            try {
                transactionService.sendMoney(sender, receiver, amount, description);
                logger.info("Transaction réussie de {} à {}", sender.getEmail(), receiver.getEmail());
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
                logger.error("Erreur lors de la transaction: {}", e.getMessage());
                return "home";
            }
        } else {
            model.addAttribute("error", "Transaction non autorisée.");
            logger.warn("Transaction non autorisée entre {} et {}", currentUser.getUsername(), connectionEmail);
            return "home";
        }
        return "redirect:/home";
    }

    /**
     * Affiche les transactions de l'utilisateur connecté.
     *
     * @param currentUser L'utilisateur actuellement connecté.
     * @param model       Le modèle pour la vue.
     * @return La page des transactions.
     */
    @GetMapping("/transactions")
    public String showTransactions(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        logger.info("Affichage des transactions pour l'utilisateur: {}", currentUser.getUsername());
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        List<Transaction> transactions = transactionService.findTransactionsForUser(user);
        model.addAttribute("transactions", transactions);
        return "transactions";
    }
}
