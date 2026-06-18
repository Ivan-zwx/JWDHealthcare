package hr.algebra.jwdhealthcare.exception;

import lombok.Getter;

/**
 * Appointment scheduling validation failures are represented for MVC form handling.
 */
@Getter
public class AppointmentSchedulingException extends RuntimeException {

    private final String fieldName;
    private final String messageKey;

    /**
     * Creates a scheduling validation exception.
     *
     * @param fieldName the form field that caused the validation failure
     * @param messageKey the internationalized message key
     */
    public AppointmentSchedulingException(String fieldName, String messageKey) {
        super(messageKey);
        this.fieldName = fieldName;
        this.messageKey = messageKey;
    }
}