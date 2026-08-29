package hr.algebra.jwdhealthcare.controller.mvc;

import hr.algebra.jwdhealthcare.dto.view.AdminUserViewDto;
import hr.algebra.jwdhealthcare.exception.UserAccountManagementException;
import hr.algebra.jwdhealthcare.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController adminUserController;

    @Test
    void listAddsUsersToModelAndReturnsListView() {
        Principal principal = () -> "admin";
        List<AdminUserViewDto> users = List.of();

        when(adminUserService.findAllForAdminView("admin")).thenReturn(users);

        Model model = new ExtendedModelMap();

        String viewName = adminUserController.list(principal, model);

        assertEquals("admin/users/list", viewName);
        assertSame(users, model.asMap().get("users"));
        verify(adminUserService).findAllForAdminView("admin");
    }

    @Test
    void toggleEnabledUpdatesUserAndRedirects() {
        Principal principal = () -> "admin";
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = adminUserController.toggleEnabled(2, principal, redirectAttributes);

        assertEquals("redirect:/admin/users", viewName);
        assertEquals(
                "admin.users.message.statusUpdated",
                redirectAttributes.getFlashAttributes().get("successMessageKey")
        );
        verify(adminUserService).toggleEnabled(2, "admin");
    }

    @Test
    void toggleEnabledAddsErrorMessageWhenServiceRejectsChange() {
        Principal principal = () -> "admin";
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        doThrow(new UserAccountManagementException("admin.users.message.selfDisableNotAllowed"))
                .when(adminUserService)
                .toggleEnabled(1, "admin");

        String viewName = adminUserController.toggleEnabled(1, principal, redirectAttributes);

        assertEquals("redirect:/admin/users", viewName);
        assertEquals(
                "admin.users.message.selfDisableNotAllowed",
                redirectAttributes.getFlashAttributes().get("errorMessageKey")
        );
    }
}