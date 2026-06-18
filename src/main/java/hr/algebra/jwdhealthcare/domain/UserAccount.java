package hr.algebra.jwdhealthcare.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A login account is represented for authentication and role-based authorization.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "UserAccount")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUserAccount")
    private Integer idUserAccount;

    @Column(name = "FullName", nullable = false, length = 300)
    private String fullName;

    @Column(name = "Email", nullable = false, length = 300)
    private String email;

    @Column(name = "Username", nullable = false, length = 300)
    private String username;

    @Column(name = "PasswordHash", nullable = false, length = 300)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false, length = 300)
    private UserRole role;

    @Column(name = "Enabled", nullable = false)
    private boolean enabled;
}