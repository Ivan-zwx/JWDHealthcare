package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.domain.UserRole;
import hr.algebra.jwdhealthcare.dto.view.AdminUserViewDto;
import hr.algebra.jwdhealthcare.exception.UserAccountManagementException;
import hr.algebra.jwdhealthcare.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void findAllForAdminViewReturnsUsers() {
        UserAccount admin = createUserAccount(1, "admin", UserRole.ADMIN, true);
        UserAccount doctor = createUserAccount(2, "doctor", UserRole.DOCTOR, true);

        when(userAccountRepository.findAllForAdminList()).thenReturn(List.of(admin, doctor));

        List<AdminUserViewDto> users = adminUserService.findAllForAdminView("admin");

        assertEquals(2, users.size());
    }

    @Test
    void toggleEnabledDisablesDifferentUser() {
        UserAccount doctor = createUserAccount(2, "doctor", UserRole.DOCTOR, true);

        when(userAccountRepository.findByIdForAdmin(2)).thenReturn(Optional.of(doctor));

        adminUserService.toggleEnabled(2, "admin");

        assertFalse(doctor.isEnabled());
        verify(userAccountRepository).save(doctor);
    }

    @Test
    void toggleEnabledRejectsDisablingCurrentUser() {
        UserAccount admin = createUserAccount(1, "admin", UserRole.ADMIN, true);

        when(userAccountRepository.findByIdForAdmin(1)).thenReturn(Optional.of(admin));

        assertThrows(
                UserAccountManagementException.class,
                () -> adminUserService.toggleEnabled(1, "admin")
        );
    }

    private UserAccount createUserAccount(
            Integer idUserAccount,
            String username,
            UserRole role,
            boolean enabled
    ) {
        UserAccount userAccount = new UserAccount();
        userAccount.setIdUserAccount(idUserAccount);
        userAccount.setFullName(username);
        userAccount.setEmail(username + "@example.com");
        userAccount.setUsername(username);
        userAccount.setPasswordHash("password-hash");
        userAccount.setRole(role);
        userAccount.setEnabled(enabled);

        return userAccount;
    }
}