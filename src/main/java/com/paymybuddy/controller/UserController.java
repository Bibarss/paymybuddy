package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.Data;

import java.util.List;

@Data
@Controller
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;

    public UserController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    // Afficher la page d'inscription
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Renvoie la vue register.html
    }

    // Gérer l'inscription de l'utilisateur
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.registerUser(user);
        return "redirect:/login"; // Redirige vers la page de connexion
    }

    // Afficher la page de connexion
    @GetMapping("/login")
    public String showLoginForm() {
        //model.addAttribute("user", new User());
        return "login"; // Renvoie la vue login.html
    }
/*
    // Gérer la connexion de l'utilisateur
    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user) {
        // Logique pour vérifier les informations de connexion
        // ...
        return "redirect:/home"; // Redirige vers la page de profil après connexion
    }
*/

    /*

    // Afficher le tableau de bord de l'utilisateur (profil)
    @GetMapping("/home")
    public String showHomePage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        model.addAttribute("user", user);
        return "home"; // Correspond à la page d'accueil / tableau de bord
    }

*/

    @GetMapping("/home")
    public String showHomePage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        List<Transaction> transactions = transactionService.findTransactionsForUser(user);
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "home"; // Correspond au fichier HTML : home.htm0l
    }

    // Afficher la page de profil
    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        model.addAttribute("user", user);
        return "profile"; // Correspond à la maquette du profil profile.html
    }


    // Modifier le profil de l'utilisateur
    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails currentUser,
                                @ModelAttribute("user") User updatedUser) {
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        if (user != null) {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword()); // À sécuriser avec encodage
            userService.updateUser(user);
        }
        return "redirect:/profile";
    }


    // Afficher la liste des connexions (amis)
    @GetMapping("/connections")
    public String showConnections(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        model.addAttribute("connections", user.getConnections());
        return "connections"; // Correspond à la maquette pour ajouter une relation
    }


    // Ajouter une nouvelle connexion
    @PostMapping("/connections/add")
    public String addConnection(@AuthenticationPrincipal UserDetails currentUser, @RequestParam("email") String email) {
        User user = userService.findByEmail(currentUser.getUsername()).orElse(null);
        User connection = userService.findByEmail(email).orElse(null);
        if (connection != null) {
            userService.addConnection(user, connection);
        }
        return "redirect:/connections";
    }



    // Afficher la page d'ajout de relation
    @GetMapping("/addConnection")
    public String showAddConnectionForm(Model model) {
        return "addConnection"; // Renvoie la vue addConnection.html
    }

    // Gérer l'ajout de relation
    @PostMapping("/addConnection")
    public String addConnection(@RequestParam String email) {
        // Logique pour ajouter la relation
        // ...
        return "redirect:/profile"; // Redirige vers la page de profil après l'ajout
    }

// Autres méthodes pour la connexion, le tableau de bord, etc.
}