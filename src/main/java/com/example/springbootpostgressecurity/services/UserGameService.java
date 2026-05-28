package com.example.springbootpostgressecurity.services;

import com.example.springbootpostgressecurity.models.game.GameRole;
import com.example.springbootpostgressecurity.models.game.Profile;
import com.example.springbootpostgressecurity.models.game.UserGame;
import com.example.springbootpostgressecurity.models.game.UserRole;
import com.example.springbootpostgressecurity.models.game.UserStatus;
import com.example.springbootpostgressecurity.payload.request.UserGameRequest;
import com.example.springbootpostgressecurity.payload.response.GameRolesResponse;
import com.example.springbootpostgressecurity.payload.response.UserGameResponse;
import com.example.springbootpostgressecurity.payload.response.UserGameStatusResponse;
import com.example.springbootpostgressecurity.repository.UserGameGroupByStatus;
import com.example.springbootpostgressecurity.repository.UserRepository;
import com.example.springbootpostgressecurity.repository.UserGameRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.Instant;

@Service
public class UserGameService {
    private final UserGameRepository userGameRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserGameService(UserGameRepository userGameRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userGameRepository = userGameRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Cacheable(value="usergames")
    public List<UserGameResponse> findAll() {
        return userGameRepository.findAll().stream()
                .map(UserGameResponse::from)
                .toList();
    }
    @Cacheable(value = "usergame", key = "#id")
    public UserGameResponse findById(Long id) {
        return UserGameResponse.from(getById(id));
    }

    @Transactional(readOnly = true)
    public UserGameResponse helloWorld(String email) {
        return userGameRepository.helloWorld(email)
                .map(UserGameResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGame not found"));
    }

    @Transactional(readOnly = true)
    public UserGameResponse helloWorld2(String email) {
        return userGameRepository.helloWorld2(email)
                .map(UserGameResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGame not found"));
    }

    @Transactional(readOnly = true)
    public List<UserGameStatusResponse> selectObject(String email) {
        return userGameRepository.selectObject(email).stream()
                .map(result -> new UserGameStatusResponse(result.getId(), result.getStatus()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByEmailContaining(String email) {
        return toResponse(userGameRepository.findByEmailContainingIgnoreCase(email));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByStatus(UserStatus status) {
        return toResponse(userGameRepository.findByStatus(status));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByRole(UserRole role) {
        return toResponse(userGameRepository.findByRole(role));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByEmailVerified(Boolean emailVerified) {
        return toResponse(userGameRepository.findByEmailVerified(emailVerified));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findLatest() {
        return toResponse(userGameRepository.findTop5ByOrderByCreatedAtDesc());
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByStatusSql(UserStatus status) {
        return toResponse(userGameRepository.findByStatusSql(status.name()));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByRoleSql(UserRole role) {
        return toResponse(userGameRepository.findByRoleSql(role.name()));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByEmailContainingSql(String email) {
        return toResponse(userGameRepository.findByEmailContainingSql(email));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findCreatedAfterSql(Instant createdAt) {
        return toResponse(userGameRepository.findCreatedAfterSql(createdAt));
    }

    @Transactional(readOnly = true)
    public List<UserGameResponse> findByEmailVerifiedAndStatusSql(Boolean emailVerified, UserStatus status) {
        return toResponse(userGameRepository.findByEmailVerifiedAndStatusSql(emailVerified, status.name()));
    }

    @Transactional(readOnly = true)
    public List<UserGameGroupByStatus> groupByStatus() {
        return userGameRepository.groupByStatus();
    }

    @Transactional(readOnly = true)
    public UserGameResponse findCurrentUserGame(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    UserGame userGame = user.getUserGame();
                    if (userGame == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGame not found");
                    }
                    return UserGameResponse.from(userGame);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    @CachePut(value = "usergame", key = "#result.id")
    public UserGameResponse create(UserGameRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (userGameRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }

        UserGame userGame = UserGame.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() == null ? UserRole.USER : request.getRole())
                .status(request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus())
                .emailVerified(request.getEmailVerified() != null && request.getEmailVerified())
                .build();

        return UserGameResponse.from(userGameRepository.save(userGame));
    }
    @CachePut(value = "usergame", key = "#id")
    public UserGameResponse update(Long id, UserGameRequest request) {
        UserGame userGame = getById(id);

        userGameRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
                });

        userGame.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            userGame.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            userGame.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            userGame.setStatus(request.getStatus());
        }
        if (request.getEmailVerified() != null) {
            userGame.setEmailVerified(request.getEmailVerified());
        }

        return UserGameResponse.from(userGameRepository.save(userGame));
    }

    @Caching(evict = {
            @CacheEvict(value = "usergame", allEntries = true),
            @CacheEvict(value = "usergames", allEntries = true)
    })
    @Transactional
    public GameRolesResponse updateCurrentUserGameRoles(Long userId, Set<GameRole> roles) {
        if (roles == null || roles.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose at least two game roles");
        }

        UserGame userGame = userGameRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGame not found"));

        Profile profile = userGame.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUserGame(userGame);
            userGame.setProfile(profile);
        }

        profile.setGameRoles(new HashSet<>(roles));
        userGameRepository.save(userGame);

        return new GameRolesResponse(profile.getGameRoles());
    }
    @CacheEvict(value = "usergame", key = "#id")
//    @CacheEvict(value = "usergames", allEntries = true)
    public void delete(Long id) {
        UserGame userGame = getById(id);
        userGameRepository.delete(userGame);
    }

    private UserGame getById(Long id) {
        return userGameRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserGame not found"));
    }

    private List<UserGameResponse> toResponse(List<UserGame> userGames) {
        return userGames.stream()
                .map(UserGameResponse::from)
                .toList();
    }
}
