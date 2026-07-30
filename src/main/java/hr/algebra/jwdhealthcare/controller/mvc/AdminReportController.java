package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Administrator report pages are handled through MVC endpoints.
 */
@Controller
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    /**
     * Displays generated reports for administrator review.
     *
     * @param model the model used by the report list view
     * @return the report list view
     */
    @GetMapping("/admin/reports")
    public String list(Model model) {
        model.addAttribute("reports", adminReportService.findAllReports());
        return "admin/reports/list";
    }

    /**
     * Generates a new appointment summary report.
     *
     * @param redirectAttributes redirect attributes used after report generation
     * @return a redirect to the report list
     */
    @PostMapping("/admin/reports/generate")
    public String generate(RedirectAttributes redirectAttributes) {
        adminReportService.generateAppointmentSummaryReport();
        redirectAttributes.addFlashAttribute("successMessageKey", "admin.reports.message.generated");

        return "redirect:/admin/reports";
    }
}