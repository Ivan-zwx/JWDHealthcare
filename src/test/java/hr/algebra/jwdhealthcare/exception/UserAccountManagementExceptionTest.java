package hr.algebra.jwdhealthcare.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAccountManagementExceptionTest {

    @Test
    void constructorStoresMessageKey() {
        UserAccountManagementException exception = new UserAccountManagementException(
                "admin.users.message.selfDisableNotAllowed"
        );

        assertEquals("admin.users.message.selfDisableNotAllowed", exception.getMessageKey());
        assertEquals("admin.users.message.selfDisableNotAllowed", exception.getMessage());
    }
}