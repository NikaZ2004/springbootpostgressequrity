package com.example.springbootpostgressecurity.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "DepartmentRequest", description = "Request body for department create/update")
public class DepartmentRequest {
    @Schema(example = "1", description = "Required on create, ignored on update")
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Engineering")
    private String name;
}
