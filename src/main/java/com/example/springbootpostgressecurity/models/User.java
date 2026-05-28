package com.example.springbootpostgressecurity.models;

import java.util.HashSet;
import java.util.Set;

import com.example.springbootpostgressecurity.models.game.UserGame;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table( name = "users",
        uniqueConstraints = {
          @UniqueConstraint(columnNames = "username"),
          @UniqueConstraint(columnNames = "email")
        },
        indexes = {
          @Index(name = "idx_users_name", columnList = "name")
        })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  @Column(name = "username", nullable = false, length = 20)
  private String username;

  @NotBlank
  @Size(max = 20)
  @Column(name = "name", nullable = false, length = 20)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Email
  @Column(name = "email", nullable = false, length = 50)
  private String email;

  @NotBlank
  @Size(max = 120)
  @Column(name = "password", nullable = false, length = 120)
  private String password;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"),
      indexes = {
          @Index(name = "idx_user_roles_role_id_user_id", columnList = "role_id, user_id")
      })
  private Set<Role> roles = new HashSet<>();

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_game_id", unique = true)
  private UserGame userGame;

  public UserGame getUserGame() {
    return userGame;
  }

  public void setUserGame(UserGame userGame) {
    this.userGame = userGame;
  }

  public User() {
  }

  public User(String username, String email, String password) {
    this.username = username;
    this.name = username;
    this.email = email;
    this.password = password;
  }

  public User(String username, String name, String email, String password) {
    this.username = username;
    this.name = name;
    this.email = email;
    this.password = password;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}
