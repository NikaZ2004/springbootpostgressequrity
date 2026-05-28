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
@Schema(name = "ProjectRequest", description = "Request body for project create/update")
public class ProjectRequest {
    @Schema(example = "1", description = "Required on create, ignored on update")
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "API Gateway")
    private String name;

    @Schema(example = "1", nullable = true)
    private Integer departmentId;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Schema(example = "50000.00")
    private BigDecimal budget;
}
