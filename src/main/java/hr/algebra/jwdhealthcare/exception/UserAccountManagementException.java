package hr.algebra.jwdhealthcare.exception;

import lombok.Getter;

/**
 * User account management validation failures are represented for administrator actions.
 */
@Getter
public class UserAccountManagementException extends RuntimeException {

    private final String messageKey;

    /**
     * Creates a user account management exception.
     *
     * @param messageKey the internationalized message key
     */
    public UserAccountManagementException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }
}