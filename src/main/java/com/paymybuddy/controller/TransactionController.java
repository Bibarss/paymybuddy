package com.paymybuddy.controller;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.Users;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UsersService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/**
 * Contrôleur pour gérer les transactions liées à l'utilisateur : lister les differentes transactions et effectuer une transaction
 */
@Data
@Controller
public class TransactionController {

    private final UsersService usersService;
    private final TransactionService transactionService;

    private static final Logger logger = LogManager.getLogger(TransactionController.class);

    public TransactionController(UsersService usersService, TransactionService transactionService) {
        this.usersService = usersService;
        this.transactionService = transactionService;
    }

    /**
     * Gère l'envoi d'argent à une connexion.
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param model          Le modèle pour la vue.
     * @return Redirige vers la page des transfers.
     */
    @GetMapping("/transfer")
    public String showTransferPage(Principal principal, Model model) {
        Users user = usersService.findByEmail(principal.getName()).orElse(null);
        model.addAttribute("user", user);
        if (user != null) {
            List<Transaction> transactions = transactionService.findTransactionsForUser(user);
            model.addAttribute("transactions", transactions);
        }
        return "transfer"; // Correspond au fichier transfer.html
    }


    /**
     * Gère l'envoi d'argent à une connexion.
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param connectionEmail L'e-mail de la connexion à laquelle envoyer de l'argent.
     * @param amount         Le montant à envoyer.
     * @param description    La description de la transaction.
     * @return Redirige vers la page d'accueil.
     */
    @PostMapping("/transactions/send")
    public String sendMoney(Principal principal,
                            @RequestParam("connectionEmail") String connectionEmail,
                            @RequestParam("amount") Double amount,
                            @RequestParam("description") String description,
                            RedirectAttributes redirectAttributes) {

        logger.info("Envoi d'argent de {} à {}", principal.getName(), connectionEmail);

        Users sender = usersService.findByEmail(principal.getName()).orElse(null);
        Users receiver = usersService.findByEmail(connectionEmail).orElse(null);

        if (sender != null && receiver != null && sender.getConnections().contains(receiver)) {
            try {
                transactionService.sendMoney(sender, receiver, amount, description);
                logger.info("Transaction réussie de {} à {}", sender.getEmail(), receiver.getEmail());
                redirectAttributes.addFlashAttribute("success", "Transaction effectuée avec succès.");
            } catch (Exception e) {
                logger.error("Erreur lors de la transaction: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        } else {
            logger.warn("Transaction non autorisée entre {} et {}", principal.getName(), connectionEmail);
            redirectAttributes.addFlashAttribute("error", "Transaction non autorisée.");
        }
        return "redirect:/transfer";
    }

}
