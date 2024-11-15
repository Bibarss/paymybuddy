package com.paymybuddy.exception;

/**
 * Exception levée lorsque l'utilisateur n'est pas trouvé.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructeur avec un message détaillé.
     *
     * @param message Le message détaillant la cause de l'exception.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
