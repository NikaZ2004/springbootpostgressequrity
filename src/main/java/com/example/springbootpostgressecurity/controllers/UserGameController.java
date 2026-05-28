package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.payload.request.UserGameRequest;
import com.example.springbootpostgressecurity.payload.request.UpdateGameRolesRequest;
import com.example.springbootpostgressecurity.models.game.UserRole;
import com.example.springbootpostgressecurity.models.game.UserStatus;
import com.example.springbootpostgressecurity.payload.response.GameRolesResponse;
import com.example.springbootpostgressecurity.payload.response.UserGameResponse;
import com.example.springbootpostgressecurity.payload.response.UserGameStatusResponse;
import com.example.springbootpostgressecurity.repository.UserGameGroupByStatus;
import com.example.springbootpostgressecurity.services.UserGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.springbootpostgressecurity.security.services.UserDetailsImpl;

import java.time.Instant;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user-games")
@Tag(name = "UserGame", description = "CRUD operations for game users")
public class UserGameController {
    private final UserGameService userGameService;

    public UserGameController(UserGameService userGameService) {
        this.userGameService = userGameService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all game users")
    public List<UserGameResponse> findAll() {
        return userGameService.findAll();
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get game user by id")
    public UserGameResponse findById(@PathVariable Long id) {
        return userGameService.findById(id);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/hello-world")
    @Operation(summary = "Get game user by email using positional native query")
    public UserGameResponse helloWorld(@RequestParam String email) {
        return userGameService.helloWorld(email);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/hello-world-2")
    @Operation(summary = "Get game user by email using named native query")
    public UserGameResponse helloWorld2(@RequestParam String email) {
        return userGameService.helloWorld2(email);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/select-object")
    @Operation(summary = "Get selected game user fields by email")
    public List<UserGameStatusResponse> selectObject(@RequestParam String email) {
        return userGameService.selectObject(email);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/keyword/email-contains")
    @Operation(summary = "Find game users by email fragment using repository keyword")
    public List<UserGameResponse> findByEmailContaining(@RequestParam String email) {
        return userGameService.findByEmailContaining(email);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/keyword/status")
    @Operation(summary = "Find game users by status using repository keyword")
    public List<UserGameResponse> findByStatus(@RequestParam UserStatus status) {
        return userGameService.findByStatus(status);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/keyword/role")
    @Operation(summary = "Find game users by role using repository keyword")
    public List<UserGameResponse> findByRole(@RequestParam UserRole role) {
        return userGameService.findByRole(role);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/keyword/email-verified")
    @Operation(summary = "Find game users by email verification flag using repository keyword")
    public List<UserGameResponse> findByEmailVerified(@RequestParam Boolean emailVerified) {
        return userGameService.findByEmailVerified(emailVerified);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/keyword/latest")
    @Operation(summary = "Find latest game users using repository keyword")
    public List<UserGameResponse> findLatest() {
        return userGameService.findLatest();
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/sql/status")
    @Operation(summary = "Find game users by status using native SQL query")
    public List<UserGameResponse> findByStatusSql(@RequestParam UserStatus status) {
        return userGameService.findByStatusSql(status);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/sql/role")
    @Operation(summary = "Find game users by role using native SQL query")
    public List<UserGameResponse> findByRoleSql(@RequestParam UserRole role) {
        return userGameService.findByRoleSql(role);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/sql/email-contains")
    @Operation(summary = "Find game users by email fragment using native SQL query")
    public List<UserGameResponse> findByEmailContainingSql(@RequestParam String email) {
        return userGameService.findByEmailContainingSql(email);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/sql/created-after")
    @Operation(summary = "Find game users created after date using native SQL query")
    public List<UserGameResponse> findCreatedAfterSql(@RequestParam Instant createdAt) {
        return userGameService.findCreatedAfterSql(createdAt);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/sql/email-verified-status")
    @Operation(summary = "Find game users by email verification flag and status using native SQL query")
    public List<UserGameResponse> findByEmailVerifiedAndStatusSql(
            @RequestParam Boolean emailVerified,
            @RequestParam UserStatus status) {
        return userGameService.findByEmailVerifiedAndStatusSql(emailVerified, status);
    }

    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/sql/group-by-status")
    @Operation(summary = "Count game users grouped by status using native SQL query")
    public List<UserGameGroupByStatus> groupByStatus() {
        return userGameService.groupByStatus();
    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user's game profile")
    public UserGameResponse findCurrentUserGame() {
        return userGameService.findCurrentUserGame(getCurrentUserId());
    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PatchMapping("/me/game-roles")
    @Operation(summary = "Update current authenticated user's game roles")
    public GameRolesResponse updateCurrentUserGameRoles(@Valid @RequestBody UpdateGameRolesRequest request) {
        return userGameService.updateCurrentUserGameRoles(getCurrentUserId(), request.getRoles());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        return userDetails.getId();
    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create game user")
    public ResponseEntity<UserGameResponse> create(@Valid @RequestBody UserGameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userGameService.create(request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Update game user")
    public UserGameResponse update(@PathVariable Long id, @Valid @RequestBody UserGameRequest request) {
        return userGameService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Delete game user")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userGameService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
