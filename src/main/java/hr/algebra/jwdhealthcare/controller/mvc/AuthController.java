package hr.algebra.jwdhealthcare.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Authentication-related MVC pages are handled.
 */
@Controller
public class AuthController {

    /**
     * Displays the login page.
     *
     * @return the name of the Thymeleaf login template
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}