package com.example.springbootpostgressecurity.payload.request;

import com.example.springbootpostgressecurity.models.game.GameRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(name = "UpdateGameRolesRequest", description = "Request body for current user's game roles")
public class UpdateGameRolesRequest {
    @NotNull
    @Size(min = 2, message = "Choose at least two game roles")
    @Schema(description = "Selected game roles", example = "[\"MID\", \"SUPPORT\"]")
    private Set<GameRole> roles;

    public Set<GameRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<GameRole> roles) {
        this.roles = roles;
    }
}
