package com.example.springbootpostgressecurity.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbootpostgressecurity.models.User;
import com.example.springbootpostgressecurity.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    log.info("userDetails loaded username {} id {}", username, user.getId());
    return UserDetailsImpl.build(user);
  }

}
