package com.example.springbootpostgressecurity.payload.request;

import java.util.Set;

import jakarta.validation.constraints.*;

public class SignupRequest {
  @Size(min = 3, max = 20)
  private String username;

  @Size(min = 3, max = 20)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  public String getUsername() {
    return hasText(username) ? username : name;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<String> getRole() {
    return this.role;
  }

  public void setRole(Set<String> role) {
    this.role = role;
  }

  @AssertTrue(message = "Username or name is required")
  public boolean isUsernameOrNamePresent() {
    return hasText(username) || hasText(name);
  }

  private boolean hasText(String value) {
    return value != null && !value.trim().isEmpty();
  }
}
