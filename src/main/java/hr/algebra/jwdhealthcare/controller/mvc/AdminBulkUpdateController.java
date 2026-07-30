package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.service.AdminBulkUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Administrator bulk update pages are handled through MVC endpoints.
 */
@Controller
@RequiredArgsConstructor
public class AdminBulkUpdateController {

    private final AdminBulkUpdateService adminBulkUpdateService;

    /**
     * Displays available administrator bulk update operations.
     *
     * @param model the model used by the bulk update view
     * @return the bulk update view
     */
    @GetMapping("/admin/bulk-updates")
    public String index(Model model) {
        model.addAttribute("preview", adminBulkUpdateService.createPastAppointmentCompletionPreview());
        return "admin/bulk-updates/index";
    }

    /**
     * Marks past scheduled appointments as completed.
     *
     * @param redirectAttributes redirect attributes used after the bulk update
     * @return a redirect to the bulk update view
     */
    @PostMapping("/admin/bulk-updates/complete-past-appointments")
    public String completePastScheduledAppointments(RedirectAttributes redirectAttributes) {
        int updatedCount = adminBulkUpdateService.completePastScheduledAppointments();
        redirectAttributes.addFlashAttribute("updatedCount", updatedCount);

        return "redirect:/admin/bulk-updates";
    }
}