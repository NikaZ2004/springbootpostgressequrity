package com.example.springbootpostgressecurity.models.game;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "userGame")
@Schema(name = "Profile", description = "User Gaming Profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Profile ID", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @Schema(hidden = true)
    private UserGame userGame;

    @Column(name = "summoner_name", length = 100)
    private String summonerName;

    @Column(length = 20)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GameRole mainRole;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "profile_game_roles",
            joinColumns = @JoinColumn(name = "profile_id"),
            indexes = {
                    @Index(name = "idx_profile_game_roles_profile_id", columnList = "profile_id"),
                    @Index(name = "idx_profile_game_roles_game_role_profile_id", columnList = "game_role, profile_id")
            })
    @Enumerated(EnumType.STRING)
    @Column(name = "game_role", nullable = false, length = 20)
    @Builder.Default
    private Set<GameRole> gameRoles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Rank rank;

    @Column(length = 500)
    private String bio;

    private Integer age;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
