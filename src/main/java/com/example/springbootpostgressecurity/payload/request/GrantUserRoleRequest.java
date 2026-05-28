package com.example.springbootpostgressecurity.payload.request;

import com.example.springbootpostgressecurity.models.ERole;
import jakarta.validation.constraints.NotNull;

public class GrantUserRoleRequest {
  @NotNull
  private ERole roleName;

  public ERole getRoleName() {
    return roleName;
  }

  public void setRoleName(ERole roleName) {
    this.roleName = roleName;
  }
}
