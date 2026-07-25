package hr.algebra.jwdhealthcare.controller.mvc.advice;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Security-related model attributes are added for MVC views.
 */
@ControllerAdvice(basePackages = "hr.algebra.jwdhealthcare.controller.mvc")
public class SecurityModelAdvice {

    /**
     * Adds current authentication and role state to every MVC model.
     *
     * @param model the MVC model used by Thymeleaf views
     */
    @ModelAttribute
    public void addSecurityAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = isAuthenticated(authentication);

        model.addAttribute("currentUserAuthenticated", authenticated);
        model.addAttribute("currentUserAdmin", hasAuthority(authentication, "ROLE_ADMIN"));
        model.addAttribute("currentUserDoctor", hasAuthority(authentication, "ROLE_DOCTOR"));
        model.addAttribute("currentUserPatient", hasAuthority(authentication, "ROLE_PATIENT"));
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean hasAuthority(Authentication authentication, String authorityName) {
        if (!isAuthenticated(authentication)) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authorityName::equals);
    }
}