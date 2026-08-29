package hr.algebra.jwdhealthcare.controller.api;

import hr.algebra.jwdhealthcare.dto.view.AdminReportViewDto;
import hr.algebra.jwdhealthcare.dto.view.AdminUserViewDto;
import hr.algebra.jwdhealthcare.service.AdminReportService;
import hr.algebra.jwdhealthcare.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminApiControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private AdminReportService adminReportService;

    @InjectMocks
    private AdminApiController adminApiController;

    @Test
    void usersReturnsAdminUsers() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "admin",
                null,
                "ROLE_ADMIN"
        );
        List<AdminUserViewDto> users = List.of();

        when(adminUserService.findAllForAdminView("admin")).thenReturn(users);

        List<AdminUserViewDto> result = adminApiController.users(authentication);

        assertSame(users, result);
        verify(adminUserService).findAllForAdminView("admin");
    }

    @Test
    void reportsReturnsGeneratedReports() {
        List<AdminReportViewDto> reports = List.of();

        when(adminReportService.findAllReports()).thenReturn(reports);

        List<AdminReportViewDto> result = adminApiController.reports();

        assertSame(reports, result);
        verify(adminReportService).findAllReports();
    }
}