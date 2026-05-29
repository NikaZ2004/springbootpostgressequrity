package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.GrantUserRoleRequest;
import com.example.springbootpostgressecurity.payload.request.UpdateUserNameRequest;
import com.example.springbootpostgressecurity.payload.response.UserInfoResponse;
import com.example.springbootpostgressecurity.payload.response.UserRoleOverviewResponse;
import com.example.springbootpostgressecurity.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "Operations for application users")
public class UserController {
  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  @Operation(summary = "Get all users")
  public List<UserInfoResponse> findAll() {
    List<UserInfoResponse> users = userService.findAll();
    log.info("users findAll count {}", users.size());
    return users;
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id:\\d+}")
  @Operation(summary = "Get user by id")
  public UserInfoResponse findById(@PathVariable Long id) {
    UserInfoResponse user = userService.findById(id);
    log.info("user findById id {} username {}", id, user.getUsername());
    return user;
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  @GetMapping("/function/by-username")
  @Operation(summary = "Get user by username using PostgreSQL function")
  public UserInfoResponse findByUsernameUsingFunction(@RequestParam String username) {
    UserInfoResponse user = userService.findByUsernameUsingFunction(username);
    log.info("user findByUsernameUsingFunction username {} id {}", username, user.getId());
    return user;
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  @GetMapping("/function/role-overview")
  @Operation(summary = "Get user role overview using PostgreSQL function")
  public List<UserRoleOverviewResponse> findRoleOverviewUsingFunction(
      @RequestParam(required = false, defaultValue = "0") Integer minRoleCount) {
    List<UserRoleOverviewResponse> overview = userService.findRoleOverviewUsingFunction(minRoleCount);
    log.info("user roleOverview function minRoleCount {} count {}", minRoleCount, overview.size());
    return overview;
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  @GetMapping("/view/role-overview")
  @Operation(summary = "Get user role overview using PostgreSQL view")
  public List<UserRoleOverviewResponse> findRoleOverviewUsingView(
      @RequestParam(required = false, defaultValue = "0") Integer minRoleCount) {
    List<UserRoleOverviewResponse> overview = userService.findRoleOverviewUsingView(minRoleCount);
    log.info("user roleOverview view minRoleCount {} count {}", minRoleCount, overview.size());
    return overview;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id:\\d+}/name")
  @Operation(summary = "Update user name using PostgreSQL procedure")
  public UserInfoResponse updateNameUsingProcedure(
      @PathVariable Long id,
      @Valid @RequestBody UpdateUserNameRequest request) {
    UserInfoResponse user = userService.updateNameUsingProcedure(id, request.getName());
    log.info("user updateName id {} username {}", id, user.getUsername());
    return user;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id:\\d+}/roles/grant")
  @Operation(summary = "Grant role to user using PostgreSQL procedure")
  public UserInfoResponse grantRoleUsingProcedure(
      @PathVariable Long id,
      @Valid @RequestBody GrantUserRoleRequest request) {
    UserInfoResponse user = userService.grantRoleUsingProcedure(id, request.getRoleName());
    log.info("user grantRole id {} role {}", id, request.getRoleName());
    return user;
  }
}
