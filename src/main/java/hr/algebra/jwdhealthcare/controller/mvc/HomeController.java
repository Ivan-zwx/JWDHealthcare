package hr.algebra.jwdhealthcare.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for public MVC pages.
 */
@Controller
public class HomeController {

    /**
     * Displays the public home page.
     *
     * @return the name of the Thymeleaf home template
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}