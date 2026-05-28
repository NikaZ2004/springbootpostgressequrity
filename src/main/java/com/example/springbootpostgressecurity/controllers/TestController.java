package com.example.springbootpostgressecurity.controllers;

import java.util.List;

import com.example.springbootpostgressecurity.payload.response.UserInfoResponse;
import com.example.springbootpostgressecurity.security.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
    }

    List<String> roles = userDetails.getAuthorities().stream()
        .map(authority -> authority.getAuthority())
        .toList();

    return ResponseEntity.ok(new UserInfoResponse(
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        roles,
        userDetails.isAccountNonExpired(),
        userDetails.isAccountNonLocked(),
        userDetails.isCredentialsNonExpired(),
        userDetails.isEnabled()));
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }
}
