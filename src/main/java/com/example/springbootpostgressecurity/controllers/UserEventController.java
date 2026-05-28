package com.example.springbootpostgressecurity.controllers;

import com.example.springbootpostgressecurity.models.clickhouse.UserEvent;
import com.example.springbootpostgressecurity.services.UserEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clickhouse/user-events")
@Tag(name = "UserEvent", description = "ClickHouse user event operations")
public class UserEventController {
    private final UserEventService userEventService;

    public UserEventController(UserEventService userEventService) {
        this.userEventService = userEventService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/schema")
    @Operation(summary = "Create ClickHouse user_events table if it does not exist")
    public ResponseEntity<Void> createTableIfNotExists() {
        userEventService.createTableIfNotExists();
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @Operation(summary = "Create user event in ClickHouse")
    public ResponseEntity<UserEvent> create(@Valid @RequestBody UserEvent request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userEventService.create(request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Get latest user events from ClickHouse")
    public List<UserEvent> findAll(@RequestParam(required = false) Integer limit) {
        return userEventService.findAll(limit);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    @Operation(summary = "Get user event by id from ClickHouse")
    public UserEvent findById(@PathVariable UUID id) {
        return userEventService.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-user")
    @Operation(summary = "Get latest user events by user id from ClickHouse")
    public List<UserEvent> findByUserId(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer limit) {
        return userEventService.findByUserId(userId, limit);
    }
}
