package com.example.springbootpostgressecurity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbootpostgressecurity.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  List<User> findByUsernameContainingIgnoreCase(String username);

  Optional<User> findByEmail(String email);

  List<User> findByEmailEndingWithIgnoreCase(String domain);

  List<User> findTop5ByOrderByIdDesc();

  List<User> findByUsernameStartingWithIgnoreCaseOrderByUsernameAsc(String prefix);

  @Query(nativeQuery = true, value = "select * from users where username = :username")
  Optional<User> findByUsernameSql(@Param("username") String username);



  @Query(nativeQuery = true, value = "select * from find_user_by_username(:username)")
  Optional<User> findByUsernameFunction(@Param("username") String username);

  @Query(nativeQuery = true, value = "select * from get_user_role_overview(:minRoleCount)")
  List<UserRoleOverviewProjection> findUserRoleOverviewFunction(@Param("minRoleCount") Integer minRoleCount);

  @Query(nativeQuery = true, value = """
      select
          id,
          username,
          name,
          email,
          email_domain as "emailDomain",
          role_count as "roleCount",
          roles
      from user_role_overview
      where role_count >= :minRoleCount
      order by role_count desc, username asc
      """)
  List<UserRoleOverviewProjection> findUserRoleOverviewView(@Param("minRoleCount") Integer minRoleCount);

  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = "call update_user_name_by_id(:userId, :name)")
  void updateUserNameByProcedure(@Param("userId") Long userId, @Param("name") String name);

  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = "call grant_role_to_user(:userId, :roleName)")
  void grantRoleToUserByProcedure(@Param("userId") Long userId, @Param("roleName") String roleName);

  @Query(nativeQuery = true, value = "select * from users where email = :email")
  Optional<User> findByEmailSql(@Param("email") String email);

  @Query(nativeQuery = true, value = "select * from users where lower(username) like lower(concat('%', :username, '%'))")
  List<User> findByUsernameContainingSql(@Param("username") String username);

  @Query(nativeQuery = true, value = "select * from users where id between :fromId and :toId order by id")
  List<User> findByIdBetweenSql(@Param("fromId") Long fromId, @Param("toId") Long toId);

  @Query(nativeQuery = true, value = """
      select u.*
      from users u
      join user_roles ur on ur.user_id = u.id
      join roles r on r.id = ur.role_id
      where r.name = :roleName
      """)
  List<User> findByRoleNameSql(@Param("roleName") String roleName);

  @Query(nativeQuery = true, value = """
      select split_part(email, '@', 2) as domain, count(*) as count
      from users
      group by split_part(email, '@', 2)
      order by count desc
      """)
  List<UserEmailDomainCount> countUsersByEmailDomainSql();
}
