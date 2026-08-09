package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.view.AdminReportViewDto;
import hr.algebra.jwdhealthcare.dto.view.AdminUserViewDto;
import hr.algebra.jwdhealthcare.service.AdminReportService;
import hr.algebra.jwdhealthcare.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administrator REST API endpoints are handled.
 */
@RestController
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminUserService adminUserService;
    private final AdminReportService adminReportService;

    /**
     * Displays user accounts for administrator API clients.
     *
     * @param authentication the current API authentication
     * @return administrator user account data
     */
    @GetMapping("/api/admin/users")
    public List<AdminUserViewDto> users(Authentication authentication) {
        return adminUserService.findAllForAdminView(authentication.getName());
    }

    /**
     * Displays generated reports for administrator API clients.
     *
     * @return generated report data
     */
    @GetMapping("/api/admin/reports")
    public List<AdminReportViewDto> reports() {
        return adminReportService.findAllReports();
    }
}