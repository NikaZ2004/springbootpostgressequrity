package com.example.springbootpostgressecurity;

import com.example.springbootpostgressecurity.models.User;
import com.example.springbootpostgressecurity.models.Department;
import com.example.springbootpostgressecurity.models.Employee;
import com.example.springbootpostgressecurity.models.game.UserGame;
import com.example.springbootpostgressecurity.models.game.UserStatus;
import com.example.springbootpostgressecurity.repository.DepartmentRepository;
import com.example.springbootpostgressecurity.repository.EmployeeDepartmentAverageProjection;
import com.example.springbootpostgressecurity.repository.EmployeeRepository;
import com.example.springbootpostgressecurity.repository.EmployeeSalaryWindowProjection;
import com.example.springbootpostgressecurity.repository.UserGameRepository;
import com.example.springbootpostgressecurity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SpringbootpostgressecurityApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserGameRepository userGameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    void signupWorksWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "signup_user",
                          "email": "signup_user@example.com",
                          "password": "123456"
                        }
                        """))
            .andExpect(status().isOk());
    }

    @Test
    void apiAuthSignupStillWorksWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "api_signup_user",
                          "email": "api_signup_user@example.com",
                          "password": "123456"
                        }
                        """))
            .andExpect(status().isOk());
    }

    @Test
    void signupAcceptsNameAsUsername() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "name_signup_user",
                          "email": "name_signup_user@example.com",
                          "password": "123456"
                        }
                        """))
            .andExpect(status().isOk());
    }

    @Test
    void signupWithExistingGameUserEmailReturnsBadRequest() throws Exception {
        UserGame userGame = new UserGame();
        userGame.setEmail("existing_game_user@example.com");
        userGame.setPasswordHash(passwordEncoder.encode("123456"));
        userGameRepository.save(userGame);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "existing_game_user",
                          "email": "existing_game_user@example.com",
                          "password": "123456"
                        }
                        """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findByStatusAndUserUsernameReturnsMatchingGameUser() {
        UserGame matchingGameUser = new UserGame();
        matchingGameUser.setEmail("status_username_match_game@example.com");
        matchingGameUser.setPasswordHash(passwordEncoder.encode("123456"));
        matchingGameUser.setStatus(UserStatus.ACTIVE);

        User matchingUser = new User(
            "status_user_match",
            "Status Match",
            "status_username_match@example.com",
            passwordEncoder.encode("123456"));
        matchingUser.setUserGame(matchingGameUser);
        userRepository.saveAndFlush(matchingUser);

        UserGame wrongStatusGameUser = new UserGame();
        wrongStatusGameUser.setEmail("status_username_wrong_status_game@example.com");
        wrongStatusGameUser.setPasswordHash(passwordEncoder.encode("123456"));
        wrongStatusGameUser.setStatus(UserStatus.BANNED);

        User wrongStatusUser = new User(
            "status_user_wrong",
            "Status Match",
            "status_username_wrong_status@example.com",
            passwordEncoder.encode("123456"));
        wrongStatusUser.setUserGame(wrongStatusGameUser);
        userRepository.saveAndFlush(wrongStatusUser);

        List<UserGame> result = userGameRepository.findByStatusAndUser_Username(
            UserStatus.ACTIVE,
            "status_user_match");

        assertThat(result)
            .extracting(UserGame::getEmail)
            .containsExactly("status_username_match_game@example.com");
    }

    @Test
    void employeeAnalyticsQueriesReturnExpectedRows() {
        Department engineering = new Department();
        engineering.setId(101);
        engineering.setName("Analytics Engineering");

        Department sales = new Department();
        sales.setId(102);
        sales.setName("Analytics Sales");
        departmentRepository.saveAll(List.of(engineering, sales));

        Employee highSalary = new Employee();
        highSalary.setId(101);
        highSalary.setFullName("Analytics High Salary");
        highSalary.setDepartment(engineering);
        highSalary.setSalary(new BigDecimal("3000.00"));

        Employee lowSalary = new Employee();
        lowSalary.setId(102);
        lowSalary.setFullName("Analytics Low Salary");
        lowSalary.setDepartment(engineering);
        lowSalary.setSalary(new BigDecimal("2000.00"));

        Employee onlySalesEmployee = new Employee();
        onlySalesEmployee.setId(103);
        onlySalesEmployee.setFullName("Analytics Sales Salary");
        onlySalesEmployee.setDepartment(sales);
        onlySalesEmployee.setSalary(new BigDecimal("2500.00"));

        employeeRepository.saveAllAndFlush(List.of(highSalary, lowSalary, onlySalesEmployee));

        List<EmployeeSalaryWindowProjection> nativeSqlWindows =
                employeeRepository.findSalaryWindowsWithNativeSql();
        EmployeeSalaryWindowProjection nativeHighSalary = nativeSqlWindows.stream()
                .filter(employee -> employee.getEmployeeId().equals(101))
                .findFirst()
                .orElseThrow();

        assertThat(nativeHighSalary.getDepartmentSalaryRank()).isEqualTo(1L);
        assertThat(nativeHighSalary.getDepartmentSalaryRowNumber()).isEqualTo(1L);
        assertThat(nativeHighSalary.getDepartmentAverageSalary()).isEqualTo(2500.0);

        List<EmployeeSalaryWindowProjection> jpqlWindows =
                employeeRepository.findSalaryWindowsWithJpql();
        EmployeeSalaryWindowProjection jpqlLowSalary = jpqlWindows.stream()
                .filter(employee -> employee.getEmployeeId().equals(102))
                .findFirst()
                .orElseThrow();

        assertThat(jpqlLowSalary.getDepartmentSalaryRank()).isEqualTo(2L);
        assertThat(jpqlLowSalary.getDepartmentSalaryRowNumber()).isEqualTo(2L);
        assertThat(jpqlLowSalary.getDepartmentAverageSalary()).isEqualTo(2500.0);

        List<EmployeeDepartmentAverageProjection> aboveAverageEmployees =
                employeeRepository.findEmployeesWithSalaryAboveDepartmentAverage();

        assertThat(aboveAverageEmployees)
                .extracting(EmployeeDepartmentAverageProjection::getEmployeeId)
                .contains(101)
                .doesNotContain(102, 103);
    }

    @Test
    void malformedSignupDoesNotReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"))
            .andExpect(status().isBadRequest());
    }
}
