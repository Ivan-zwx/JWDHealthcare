package hr.algebra.jwdhealthcare.controller.mvc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    private final AuthController authController = new AuthController();

    @Test
    void loginReturnsLoginView() {
        String viewName = authController.login();

        assertEquals("auth/login", viewName);
    }
}