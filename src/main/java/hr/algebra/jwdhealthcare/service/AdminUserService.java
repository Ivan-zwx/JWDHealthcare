package hr.algebra.jwdhealthcare.service;

import hr.algebra.jwdhealthcare.domain.UserAccount;
import hr.algebra.jwdhealthcare.dto.view.AdminUserViewDto;
import hr.algebra.jwdhealthcare.exception.UserAccountManagementException;
import hr.algebra.jwdhealthcare.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Administrator user account operations are coordinated between MVC controllers and repositories.
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserAccountRepository userAccountRepository;

    /**
     * Finds all user accounts prepared for administrator display.
     *
     * @param currentUsername the authenticated administrator username
     * @return user account view data
     */
    @Transactional(readOnly = true)
    public List<AdminUserViewDto> findAllForAdminView(String currentUsername) {
        return userAccountRepository.findAllForAdminList()
                .stream()
                .map(userAccount -> toAdminUserViewDto(userAccount, currentUsername))
                .toList();
    }

    /**
     * Toggles whether a user account is enabled.
     *
     * @param idUserAccount the user account identifier
     * @param currentUsername the authenticated administrator username
     */
    @Transactional
    public void toggleEnabled(Integer idUserAccount, String currentUsername) {
        UserAccount userAccount = userAccountRepository.findByIdForAdmin(idUserAccount)
                .orElseThrow(() -> new IllegalArgumentException("User account was not found."));

        if (userAccount.getUsername().equals(currentUsername)) {
            throw new UserAccountManagementException("admin.users.message.selfDisableNotAllowed");
        }

        userAccount.setEnabled(!userAccount.isEnabled());
        userAccountRepository.save(userAccount);
    }

    private AdminUserViewDto toAdminUserViewDto(UserAccount userAccount, String currentUsername) {
        return new AdminUserViewDto(
                userAccount.getIdUserAccount(),
                userAccount.getFullName(),
                userAccount.getEmail(),
                userAccount.getUsername(),
                userAccount.getRole(),
                userAccount.isEnabled(),
                userAccount.getUsername().equals(currentUsername)
        );
    }
}