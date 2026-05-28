package com.example.springbootpostgressecurity.models.clickhouse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserEvent", description = "ClickHouse user event")
public class UserEvent {
    @Schema(example = "2d7b7d7f-2f8f-4e92-9a8d-51f43f4f4f38", nullable = true)
    private UUID id;

    @Positive
    @Schema(example = "1")
    private Long userId;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "SIGN_IN")
    private String eventType;

    @Schema(example = "2026-05-22T10:30:00Z", nullable = true)
    private Instant eventTime;

    @Size(max = 10000)
    @Schema(example = "{\"ip\":\"127.0.0.1\"}", nullable = true)
    private String payload;
}
