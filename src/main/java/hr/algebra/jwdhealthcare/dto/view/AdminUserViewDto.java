package hr.algebra.jwdhealthcare.dto.view;

import hr.algebra.jwdhealthcare.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User account display data is transferred from services to administrator MVC views.
 */
@Getter
@AllArgsConstructor
public class AdminUserViewDto {

    private final Integer idUserAccount;
    private final String fullName;
    private final String email;
    private final String username;
    private final UserRole role;
    private final boolean enabled;
    private final boolean currentUser;
}