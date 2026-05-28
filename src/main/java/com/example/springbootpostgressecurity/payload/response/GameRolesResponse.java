package com.example.springbootpostgressecurity.payload.response;

import com.example.springbootpostgressecurity.models.game.GameRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(name = "GameRolesResponse", description = "Current user's selected game roles")
public class GameRolesResponse {
    private Set<GameRole> roles;

    public GameRolesResponse(Set<GameRole> roles) {
        this.roles = roles;
    }

    public Set<GameRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<GameRole> roles) {
        this.roles = roles;
    }
}
