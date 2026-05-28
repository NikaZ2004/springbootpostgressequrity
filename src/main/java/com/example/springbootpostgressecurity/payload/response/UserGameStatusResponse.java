package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.models.game.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserGameStatusResponse", description = "Selected game user status data")
public record UserGameStatusResponse(
        Long id,
        UserStatus status
) {
}
