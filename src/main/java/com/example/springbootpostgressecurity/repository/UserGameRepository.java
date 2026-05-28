package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.game.UserGame;
import com.example.springbootpostgressecurity.models.game.UserRole;
import com.example.springbootpostgressecurity.models.game.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserGameRepository extends JpaRepository<UserGame, Long> {
    Optional<UserGame> findByEmail(String email);

    Optional<UserGame> findByUser_Id(Long userId);

    boolean existsByEmail(String email);

    Optional<UserGame> findUserGameByRole(UserRole role);

    List<UserGame> findByEmailContainingIgnoreCase(String email);

    List<UserGame> findByStatus(UserStatus status);

    List<UserGame> findByStatusAndUser_Username(UserStatus status, String username);

    @Query("""
            select u.name as name, ug.status as status
            from UserGame ug
            join ug.user u
            where u.username = :username
            """)
    List<UserGameNameStatusProjection> findNameAndStatusByUsernameJoin(@Param("username") String username);

    List<UserGame> findByRole(UserRole role);

    List<UserGame> findByEmailVerified(Boolean emailVerified);

    List<UserGame> findTop5ByOrderByCreatedAtDesc();

    @Query(nativeQuery = true, value = "SELECT * FROM game_users WHERE email=?1")
    Optional<UserGame> helloWorld(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT * FROM game_users WHERE email=:email")
    Optional<UserGame> helloWorld2(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT id, status FROM game_users WHERE email=:email")
    List<UserGameStatusProjection> selectObject(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT * FROM game_users WHERE status = :status")
    List<UserGame> findByStatusSql(@Param("status") String status);

    @Query(nativeQuery = true, value = "SELECT * FROM game_users WHERE role = :role")
    List<UserGame> findByRoleSql(@Param("role") String role);

    @Query(nativeQuery = true, value = "SELECT * FROM game_users WHERE lower(email) LIKE lower(concat('%', :email, '%'))")
    List<UserGame> findByEmailContainingSql(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT * FROM game_users WHERE created_at >= :createdAt ORDER BY created_at DESC")
    List<UserGame> findCreatedAfterSql(@Param("createdAt") Instant createdAt);

    @Query(nativeQuery = true, value = "SELECT * FROM game_users WHERE email_verified = :emailVerified AND status = :status")
    List<UserGame> findByEmailVerifiedAndStatusSql(
            @Param("emailVerified") Boolean emailVerified,
            @Param("status") String status);

    @Query(nativeQuery = true, value = "SELECT status, COUNT(*) AS count FROM game_users GROUP BY status")
    List<UserGameGroupByStatus> groupByStatus();

}
