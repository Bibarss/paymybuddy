package com.paymybuddy.controller;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.exception.TransactionException;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour gérer les transactions liées à l'utilisateur :
 * lister les transactions et effectuer une transaction.
 */
@Controller
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    /**
     * Constructeur avec injection des services.
     *
     * @param userService        Le service utilisateur.
     * @param transactionService Le service de transaction.
     */
    public TransactionController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * Gère l'affichage de la page de transfert.
     *
     * @param principal L'utilisateur actuellement connecté.
     * @param model     Le modèle pour la vue.
     * @return Le nom de la vue pour le transfert.
     */
    @GetMapping("/transfer")
    public String showTransferPage(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé: " + principal.getName()));


        // Formater le solde de l'utilisateur
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedBalance = decimalFormat.format(user.getBalance());

        model.addAttribute("formattedBalance", formattedBalance);
        model.addAttribute("user", user);

        // Récupérer les transactions et formater les montants
        List<Transaction> transactions = transactionService.findTransactionsForUser(user);


        // Créer une liste pour stocker les transactions avec montants formatés
        List<Map<String, Object>> formattedTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("transaction", transaction);
            transactionMap.put("formattedAmount", decimalFormat.format(transaction.getAmount()));
            formattedTransactions.add(transactionMap);
        }

        model.addAttribute("transactions", formattedTransactions);

        return "transfer";
    }

    /**
     * Gère l'envoi d'argent à une connexion.
     *
     * @param principal       L'utilisateur actuellement connecté.
     * @param connectionEmail L'e-mail de la connexion à laquelle envoyer de l'argent.
     * @param amount         Le montant à envoyer.
     * @param description     La description de la transaction.
     * @param model           Le modèle pour la vue.
     * @return Le nom de la vue pour le transfert avec un message de succès ou d'erreur.
     */
    @PostMapping("/transactions/send")
    public String sendMoney(Principal principal,
                            @RequestParam("connectionEmail") String connectionEmail,
                            @RequestParam("amount") BigDecimal amount,
                            @RequestParam("description") String description,
                            Model model) {

        logger.info("Envoi d'argent de {} à {}", principal.getName(), connectionEmail);

        User sender = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur expéditeur non trouvé: " + principal.getName()));
        User receiver = userService.findByEmail(connectionEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur destinataire non trouvé: " + connectionEmail));

        // Ajout des attributs nécessaires au modèle
        model.addAttribute("user", sender);

        // Formatage initial du solde
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedBalance = decimalFormat.format(sender.getBalance());
        model.addAttribute("formattedBalance", formattedBalance);

        // Récupérer et formater les transactions
        List<Transaction> transactions = transactionService.findTransactionsForUser(sender);
        List<Map<String, Object>> formattedTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("transaction", transaction);
            transactionMap.put("formattedAmount", decimalFormat.format(transaction.getAmount()));
            formattedTransactions.add(transactionMap);
        }
        model.addAttribute("transactions", formattedTransactions);

        // Validation du montant
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Montant invalide fourni par l'utilisateur: {}", amount);
            model.addAttribute("error", "Le montant doit être supérieur à zéro.");
            return "transfer";
        }

        // Vérifier si les utilisateurs sont connectés
        if (!sender.getConnections().contains(receiver)) {
            logger.warn("Transaction non autorisée entre {} et {}", principal.getName(), connectionEmail);
            model.addAttribute("error", "Transaction non autorisée.");
            return "transfer";
        }

        // Tenter d'effectuer la transaction
        try {
            transactionService.sendMoney(sender, receiver, amount, description);
            logger.info("Transaction réussie de {} à {}", sender.getEmail(), receiver.getEmail());
            model.addAttribute("success", "Transaction réussie.");
        } catch (InsufficientBalanceException e) {
            logger.error("Erreur lors de la transaction: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        } catch (TransactionException e) {
            logger.error("Erreur lors de la transaction: {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de la transaction.");
        }

        // Recalculer et ajouter 'formattedBalance' après la transaction
        formattedBalance = decimalFormat.format(sender.getBalance());
        model.addAttribute("formattedBalance", formattedBalance);

        // Rechercher et formater les transactions mises à jour
        transactions = transactionService.findTransactionsForUser(sender);
        formattedTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("transaction", transaction);
            transactionMap.put("formattedAmount", decimalFormat.format(transaction.getAmount()));
            formattedTransactions.add(transactionMap);
        }
        model.addAttribute("transactions", formattedTransactions);

        return "transfer";
    }

}
