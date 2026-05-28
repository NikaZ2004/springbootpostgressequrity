package com.example.springbootpostgressecurity.payload.request;

import com.example.springbootpostgressecurity.models.game.UserRole;
import com.example.springbootpostgressecurity.models.game.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserGameRequest", description = "Request body for game user create/update")
public class UserGameRequest {
    @NotBlank
    @Email
    @Size(max = 255)
    @Schema(example = "player1@example.com")
    private String email;

    @Size(min = 6, max = 120)
    @Schema(example = "123456", description = "Required on create, optional on update")
    private String password;

    @Schema(example = "USER")
    private UserRole role;

    @Schema(example = "ACTIVE")
    private UserStatus status;

    @Schema(example = "false")
    private Boolean emailVerified;
}
