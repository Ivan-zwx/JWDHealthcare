package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.view.AdminBulkUpdatePreviewDto;
import hr.algebra.jwdhealthcare.service.AdminBulkUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBulkUpdateControllerTest {

    @Mock
    private AdminBulkUpdateService adminBulkUpdateService;

    @InjectMocks
    private AdminBulkUpdateController adminBulkUpdateController;

    @Test
    void indexAddsPreviewToModelAndReturnsIndexView() {
        AdminBulkUpdatePreviewDto preview = new AdminBulkUpdatePreviewDto(
                2,
                LocalDateTime.of(2026, 8, 29, 12, 0)
        );

        when(adminBulkUpdateService.createPastAppointmentCompletionPreview()).thenReturn(preview);

        Model model = new ExtendedModelMap();

        String viewName = adminBulkUpdateController.index(model);

        assertEquals("admin/bulk-updates/index", viewName);
        assertSame(preview, model.asMap().get("preview"));
    }

    @Test
    void completePastScheduledAppointmentsUpdatesAndRedirects() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        when(adminBulkUpdateService.completePastScheduledAppointments()).thenReturn(3);

        String viewName = adminBulkUpdateController.completePastScheduledAppointments(redirectAttributes);

        assertEquals("redirect:/admin/bulk-updates", viewName);
        assertEquals(3, redirectAttributes.getFlashAttributes().get("updatedCount"));
        verify(adminBulkUpdateService).completePastScheduledAppointments();
    }
}