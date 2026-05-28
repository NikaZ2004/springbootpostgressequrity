package com.example.springbootpostgressecurity.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "ProjectAssignmentRequest", description = "Request body for project assignment create/update")
public class ProjectAssignmentRequest {
    @Schema(example = "1", description = "Required on create, ignored on update")
    private Integer id;

    @NotNull
    @Schema(example = "1")
    private Integer employeeId;

    @NotNull
    @Schema(example = "1")
    private Integer projectId;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Backend Developer")
    private String role;

    @NotNull
    @Min(1)
    @Max(80)
    @Schema(example = "24")
    private Integer weeklyHours;
}
