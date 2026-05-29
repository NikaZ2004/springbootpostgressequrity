package com.example.springbootpostgressecurity.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootpostgressecurity.models.ERole;
import com.example.springbootpostgressecurity.models.Role;
import com.example.springbootpostgressecurity.models.User;
import com.example.springbootpostgressecurity.models.game.UserGame;
import com.example.springbootpostgressecurity.payload.request.LoginRequest;
import com.example.springbootpostgressecurity.payload.request.SignupRequest;
import com.example.springbootpostgressecurity.payload.response.JwtResponse;
import com.example.springbootpostgressecurity.payload.response.MessageResponse;
import com.example.springbootpostgressecurity.repository.RoleRepository;
import com.example.springbootpostgressecurity.repository.UserGameRepository;
import com.example.springbootpostgressecurity.repository.UserRepository;
import com.example.springbootpostgressecurity.security.jwt.JwtUtils;
import com.example.springbootpostgressecurity.security.services.UserDetailsImpl;
import com.example.springbootpostgressecurity.services.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserGameRepository userGameRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  UserService userService;

  @PostMapping({"/api/auth/signin", "/signin"})
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("user signin attempt username {}", loginRequest.getUsername());
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        .collect(Collectors.toList());
    log.info("user login {} roles {}", userDetails.getUsername(), roles);
    return ResponseEntity
        .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping({"/api/auth/signup", "/signup"})
  @Transactional
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    String username = signUpRequest.getUsername();
    String name = hasText(signUpRequest.getName()) ? signUpRequest.getName() : username;
    log.info("user signup attempt username {} email {}", username, signUpRequest.getEmail());

    if (userRepository.existsByUsername(username)) {
      log.warn("user signup rejected username already taken {}", username);
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      log.warn("user signup rejected email already in users {}", signUpRequest.getEmail());
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    if (userGameRepository.existsByEmail(signUpRequest.getEmail())) {
      log.warn("user signup rejected email already in userGames {}", signUpRequest.getEmail());
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    String encodedPassword = encoder.encode(signUpRequest.getPassword());
    User user = new User(username, name, signUpRequest.getEmail(), encodedPassword);

    UserGame userGame = new UserGame();
    userGame.setEmail(signUpRequest.getEmail());
    userGame.setPasswordHash(encodedPassword);
    user.setUserGame(userGame);
    userGame.setUser(user);

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userService.save(user);
    log.info("user signup completed username {} email {} roles {}", username, signUpRequest.getEmail(),
        roles.stream().map(role -> role.getName().name()).toList());

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  private boolean hasText(String value) {
    return value != null && !value.trim().isEmpty();
  }
}
