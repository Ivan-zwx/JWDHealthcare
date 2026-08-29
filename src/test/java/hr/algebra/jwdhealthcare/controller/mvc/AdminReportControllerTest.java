package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.view.AdminReportViewDto;
import hr.algebra.jwdhealthcare.service.AdminReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReportControllerTest {

    @Mock
    private AdminReportService adminReportService;

    @InjectMocks
    private AdminReportController adminReportController;

    @Test
    void listAddsReportsToModelAndReturnsListView() {
        List<AdminReportViewDto> reports = List.of();

        when(adminReportService.findAllReports()).thenReturn(reports);

        Model model = new ExtendedModelMap();

        String viewName = adminReportController.list(model);

        assertEquals("admin/reports/list", viewName);
        assertSame(reports, model.asMap().get("reports"));
    }

    @Test
    void generateCreatesReportAndRedirectsToReportsPage() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminReportController.generate(redirectAttributes);

        assertEquals("redirect:/admin/reports", viewName);
        assertEquals(
                "admin.reports.message.generated",
                redirectAttributes.getFlashAttributes().get("successMessageKey")
        );
        verify(adminReportService).generateAppointmentSummaryReport();
    }
}