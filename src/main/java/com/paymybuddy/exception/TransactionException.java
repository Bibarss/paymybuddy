package com.paymybuddy.exception;

/**
 * Exception générale pour les erreurs liées aux transactions.
 */
public class TransactionException extends Exception {

    /**
     * Constructeur avec un message détaillé.
     *
     * @param message Le message détaillant la cause de l'exception.
     */
    public TransactionException(String message) {
        super(message);
    }
}
