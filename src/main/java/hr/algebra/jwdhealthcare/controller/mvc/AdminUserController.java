package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.exception.UserAccountManagementException;
import hr.algebra.jwdhealthcare.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Administrator user account pages are handled through MVC endpoints.
 */
@Controller
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Displays all user accounts for administrator review.
     *
     * @param principal the authenticated user principal
     * @param model the model used by the user account list view
     * @return the user account list view
     */
    @GetMapping("/admin/users")
    public String list(Principal principal, Model model) {
        model.addAttribute("users", adminUserService.findAllForAdminView(principal.getName()));
        return "admin/users/list";
    }

    /**
     * Toggles whether a user account is enabled.
     *
     * @param id the user account identifier
     * @param principal the authenticated user principal
     * @param redirectAttributes redirect attributes used after the action
     * @return a redirect to the user account list
     */
    @PostMapping("/admin/users/toggle-enabled/{id}")
    public String toggleEnabled(
            @PathVariable("id") Integer id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminUserService.toggleEnabled(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessageKey", "admin.users.message.statusUpdated");
        } catch (UserAccountManagementException exception) {
            redirectAttributes.addFlashAttribute("errorMessageKey", exception.getMessageKey());
        }

        return "redirect:/admin/users";
    }
}