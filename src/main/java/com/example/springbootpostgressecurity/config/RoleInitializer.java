package com.example.springbootpostgressecurity.config;

import com.example.springbootpostgressecurity.models.ERole;
import com.example.springbootpostgressecurity.models.Role;
import com.example.springbootpostgressecurity.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
  private final RoleRepository roleRepository;

  public RoleInitializer(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public void run(String... args) {
    for (ERole roleName : ERole.values()) {
      roleRepository.findByName(roleName)
          .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }
  }
}
