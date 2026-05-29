package com.example.springbootpostgressecurity.services;

import com.example.springbootpostgressecurity.models.ERole;
import com.example.springbootpostgressecurity.models.User;
import com.example.springbootpostgressecurity.payload.response.UserInfoResponse;
import com.example.springbootpostgressecurity.payload.response.UserRoleOverviewResponse;
import com.example.springbootpostgressecurity.repository.UserRepository;
import com.example.springbootpostgressecurity.repository.UserRoleOverviewProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@Service
public class UserService {
  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private static final Duration USER_CACHE_TTL = Duration.ofMinutes(30);
  private static final String USERS_KEY = "users:all";
  private static final String USER_BY_ID_PREFIX = "users:id:";
  private static final String USER_BY_USERNAME_PREFIX = "users:username:";

  private final UserRepository userRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  public UserService(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
    this.userRepository = userRepository;
    this.redisTemplate = redisTemplate;
  }

  @Transactional(readOnly = true)
  public List<UserInfoResponse> findAll() {
    Object cachedUsers = redisTemplate.opsForValue().get(USERS_KEY);
    if (cachedUsers instanceof List<?> users && users.stream().allMatch(UserInfoResponse.class::isInstance)) {
      log.info("users cache hit key {} count {}", USERS_KEY, users.size());
      return users.stream()
          .map(UserInfoResponse.class::cast)
          .toList();
    }

    List<UserInfoResponse> users = userRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
    redisTemplate.opsForValue().set(USERS_KEY, users, USER_CACHE_TTL);
    log.info("users loaded from repository count {}", users.size());
    return users;
  }

  @Transactional(readOnly = true)
  public UserInfoResponse findById(Long id) {
    String key = userByIdKey(id);
    Object cachedUser = redisTemplate.opsForValue().get(key);
    if (cachedUser instanceof UserInfoResponse user) {
      log.info("user cache hit id {} username {}", id, user.getUsername());
      return user;
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    UserInfoResponse response = toResponse(user);
    cacheUser(response);
    log.info("user loaded by id {} username {}", id, response.getUsername());
    return response;
  }

  @Transactional(readOnly = true)
  public UserInfoResponse findByUsername(String username) {
    String key = userByUsernameKey(username);
    Object cachedUser = redisTemplate.opsForValue().get(key);
    if (cachedUser instanceof UserInfoResponse user) {
      log.info("user cache hit username {} id {}", username, user.getId());
      return user;
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    UserInfoResponse response = toResponse(user);
    cacheUser(response);
    log.info("user loaded by username {} id {}", username, response.getId());
    return response;
  }

  @Transactional(readOnly = true)
  public UserInfoResponse findByUsernameUsingFunction(String username) {
    User user = userRepository.findByUsernameFunction(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    UserInfoResponse response = toResponse(user);
    cacheUser(response);
    log.info("user loaded by username function {} id {}", username, response.getId());
    return response;
  }

  @Transactional(readOnly = true)
  public List<UserRoleOverviewResponse> findRoleOverviewUsingFunction(Integer minRoleCount) {
    int normalizedMinRoleCount = normalizeMinRoleCount(minRoleCount);

    List<UserRoleOverviewResponse> users = userRepository.findUserRoleOverviewFunction(normalizedMinRoleCount).stream()
        .map(this::toRoleOverviewResponse)
        .toList();
    log.info("user roleOverview function minRoleCount {} count {}", normalizedMinRoleCount, users.size());
    return users;
  }

  @Transactional(readOnly = true)
  public List<UserRoleOverviewResponse> findRoleOverviewUsingView(Integer minRoleCount) {
    int normalizedMinRoleCount = normalizeMinRoleCount(minRoleCount);

    List<UserRoleOverviewResponse> users = userRepository.findUserRoleOverviewView(normalizedMinRoleCount).stream()
        .map(this::toRoleOverviewResponse)
        .toList();
    log.info("user roleOverview view minRoleCount {} count {}", normalizedMinRoleCount, users.size());
    return users;
  }

  @Transactional
  public UserInfoResponse updateNameUsingProcedure(Long id, String name) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    userRepository.updateUserNameByProcedure(id, name);

    UserInfoResponse response = toResponse(user);
    response.setName(name);
    evictUser(user);
    cacheUser(response);
    log.info("user name updated id {} username {}", id, response.getUsername());
    return response;
  }

  @Transactional
  public UserInfoResponse grantRoleUsingProcedure(Long id, ERole roleName) {
    if (!userRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    userRepository.grantRoleToUserByProcedure(id, roleName.name());

    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    UserInfoResponse response = toResponse(user);
    evictUser(user);
    cacheUser(response);
    log.info("user role granted id {} role {}", id, roleName);
    return response;
  }

  @Transactional
  public User save(User user) {
    User savedUser = userRepository.save(user);
    evictUser(savedUser);
    log.info("user saved id {} username {}", savedUser.getId(), savedUser.getUsername());
    return savedUser;
  }

  @Transactional
  public void delete(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    userRepository.delete(user);
    evictUser(user);
    log.info("user deleted id {} username {}", user.getId(), user.getUsername());
  }

  public void evictUser(User user) {
    try {
      redisTemplate.delete(USERS_KEY);
      redisTemplate.delete(userByIdKey(user.getId()));
      redisTemplate.delete(userByUsernameKey(user.getUsername()));
      log.info("user cache evicted id {} username {}", user.getId(), user.getUsername());
    } catch (RuntimeException ex) {
      log.warn("Failed to evict user cache", ex);
    }
  }

  private void cacheUser(UserInfoResponse user) {
    redisTemplate.opsForValue().set(userByIdKey(user.getId()), user, USER_CACHE_TTL);
    redisTemplate.opsForValue().set(userByUsernameKey(user.getUsername()), user, USER_CACHE_TTL);
    log.info("user cached id {} username {}", user.getId(), user.getUsername());
  }

  private UserRoleOverviewResponse toRoleOverviewResponse(UserRoleOverviewProjection projection) {
    return new UserRoleOverviewResponse(
        projection.getId(),
        projection.getUsername(),
        projection.getName(),
        projection.getEmail(),
        projection.getEmailDomain(),
        projection.getRoleCount(),
        projection.getRoles()
    );
  }

  private int normalizeMinRoleCount(Integer minRoleCount) {
    return minRoleCount == null ? 0 : Math.max(minRoleCount, 0);
  }

  private UserInfoResponse toResponse(User user) {
    List<String> roles = user.getRoles().stream()
        .map(role -> role.getName().name())
        .sorted()
        .toList();

    return new UserInfoResponse(
        user.getId(),
        user.getUsername(),
        user.getName(),
        user.getEmail(),
        roles,
        true,
        true,
        true,
        true
    );
  }

  private String userByIdKey(Long id) {
    return USER_BY_ID_PREFIX + id;
  }

  private String userByUsernameKey(String username) {
    return USER_BY_USERNAME_PREFIX + username;
  }
}
