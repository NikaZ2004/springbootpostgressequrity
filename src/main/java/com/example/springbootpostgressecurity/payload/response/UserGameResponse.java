package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.models.game.UserGame;
import com.example.springbootpostgressecurity.models.game.UserRole;
import com.example.springbootpostgressecurity.models.game.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@Schema(name = "UserGameResponse", description = "Game user data")
public class UserGameResponse {
    private Long id;
    private String email;
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserGameResponse from(UserGame userGame) {
        return UserGameResponse.builder()
                .id(userGame.getId())
                .email(userGame.getEmail())
                .role(userGame.getRole())
                .status(userGame.getStatus())
                .emailVerified(userGame.getEmailVerified())
                .lastLoginAt(userGame.getLastLoginAt())
                .createdAt(userGame.getCreatedAt())
                .updatedAt(userGame.getUpdatedAt())
                .build();
    }
}
