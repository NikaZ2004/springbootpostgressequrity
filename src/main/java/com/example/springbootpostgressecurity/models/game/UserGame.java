package com.example.springbootpostgressecurity.models.game;

import com.example.springbootpostgressecurity.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "game_users", indexes = {
        @Index(name = "idx_game_users_email", columnList = "email", unique = true),
        @Index(name = "idx_game_users_status_email_verified", columnList = "status, email_verified"),
        @Index(name = "idx_game_users_role", columnList = "role"),
        @Index(name = "idx_game_users_created_at_desc", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"passwordHash", "user"})
@Schema(name = "User", description = "Platform user")
public class UserGame implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    @Schema(description = "User Email", example = "player1@example.com")
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    @JsonIgnore
    @Schema(description = "Password hash", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    @Schema(description = "User role", example = "USER")
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    @Schema(description = "User status", example = "ACTIVE")
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    @Schema(description = "Email confirmation flag", example = "false")
    private Boolean emailVerified = false;

    @Column(name = "last_login_at")
    @Schema(description = "Last login time", example = "2026-05-03T17:30:00Z", nullable = true)
    private Instant lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Date of creation", example = "2026-05-03T17:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Date of last update", example = "2026-05-03T17:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    @OneToOne(mappedBy = "userGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "User Profile", hidden = true)
    private Profile profile;

    @OneToOne(mappedBy = "userGame", fetch = FetchType.LAZY)
    @JsonIgnore
    @Schema(description = "Auth user", hidden = true)
    private User user;

    public UserGame(String email, String passwordHash, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.authorities = authorities;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Transient
    private Collection<? extends GrantedAuthority> authorities;
}
