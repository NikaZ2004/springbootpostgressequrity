package com.example.springbootpostgressecurity.payload.response;

public class UserRoleOverviewResponse {
  private Long id;
  private String username;
  private String name;
  private String email;
  private String emailDomain;
  private Long roleCount;
  private String roles;

  public UserRoleOverviewResponse() {
  }

  public UserRoleOverviewResponse(Long id, String username, String name, String email,
      String emailDomain, Long roleCount, String roles) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.email = email;
    this.emailDomain = emailDomain;
    this.roleCount = roleCount;
    this.roles = roles;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
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

  public String getEmailDomain() {
    return emailDomain;
  }

  public void setEmailDomain(String emailDomain) {
    this.emailDomain = emailDomain;
  }

  public Long getRoleCount() {
    return roleCount;
  }

  public void setRoleCount(Long roleCount) {
    this.roleCount = roleCount;
  }

  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }
}
