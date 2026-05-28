package com.example.springbootpostgressecurity.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(name = "EmployeeRequest", description = "Request body for employee create/update")
public class EmployeeRequest {
    @Schema(example = "1", description = "Required on create, ignored on update")
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Anna Ivanova")
    private String fullName;

    @Schema(example = "1", nullable = true)
    private Integer departmentId;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @Schema(example = "3200.00")
    private BigDecimal salary;
}
