package com.paymybuddy.exception;

/**
 * Exception levée lorsque le solde est insuffisant pour effectuer une transaction.
 */
public class InsufficientBalanceException extends TransactionException {

    /**
     * Constructeur avec un message détaillé.
     *
     * @param message Le message détaillant la cause de l'exception.
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
