package hr.algebra.jwdhealthcare.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentSchedulingExceptionTest {

    @Test
    void constructorStoresFieldNameAndMessageKey() {
        AppointmentSchedulingException exception = new AppointmentSchedulingException(
                "scheduledAt",
                "validation.appointment.fullHour"
        );

        assertEquals("scheduledAt", exception.getFieldName());
        assertEquals("validation.appointment.fullHour", exception.getMessageKey());
        assertEquals("validation.appointment.fullHour", exception.getMessage());
    }
}