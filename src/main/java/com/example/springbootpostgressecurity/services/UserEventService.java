package com.example.springbootpostgressecurity.services;

import com.example.springbootpostgressecurity.models.clickhouse.UserEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserEventService {
    private static final Logger log = LoggerFactory.getLogger(UserEventService.class);

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 1000;
    private static final String SELECT_COLUMNS = """
            SELECT id, user_id, event_type, event_time, payload
            FROM user_events
            """;
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS user_events (
                id UUID,
                user_id UInt64,
                event_type String,
                event_time DateTime64(3, 'UTC'),
                payload String
            )
            ENGINE = MergeTree
            ORDER BY (user_id, event_time, id)
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<UserEvent> userEventRowMapper = (rs, rowNum) -> UserEvent.builder()
            .id(UUID.fromString(rs.getString("id")))
            .userId(rs.getLong("user_id"))
            .eventType(rs.getString("event_type"))
            .eventTime(rs.getTimestamp("event_time").toInstant())
            .payload(rs.getString("payload"))
            .build();

    public UserEventService(@Qualifier("clickHouseJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTableIfNotExists() {
        jdbcTemplate.execute(CREATE_TABLE_SQL);
        log.info("userEvents table ensured");
    }

    public UserEvent create(UserEvent request) {
        createTableIfNotExists();
        UserEvent userEvent = normalizeForCreate(request);

        jdbcTemplate.update("""
                INSERT INTO user_events (id, user_id, event_type, event_time, payload)
                VALUES (?, ?, ?, ?, ?)
                """,
                userEvent.getId().toString(),
                userEvent.getUserId(),
                userEvent.getEventType(),
                Timestamp.from(userEvent.getEventTime()),
                userEvent.getPayload());

        log.info("userEvent inserted id {} userId {} eventType {}", userEvent.getId(), userEvent.getUserId(),
                userEvent.getEventType());
        return userEvent;
    }

    public List<UserEvent> findAll(Integer limit) {
        createTableIfNotExists();
        return jdbcTemplate.query(
                SELECT_COLUMNS + " ORDER BY event_time DESC LIMIT " + normalizeLimit(limit),
                userEventRowMapper);
    }

    public List<UserEvent> findByUserId(Long userId, Integer limit) {
        createTableIfNotExists();
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must be positive");
        }

        return jdbcTemplate.query(
                SELECT_COLUMNS + " WHERE user_id = ? ORDER BY event_time DESC LIMIT " + normalizeLimit(limit),
                userEventRowMapper,
                userId);
    }

    public UserEvent findById(UUID id) {
        createTableIfNotExists();
        return jdbcTemplate.query(
                        SELECT_COLUMNS + " WHERE id = toUUID(?) LIMIT 1",
                        userEventRowMapper,
                        id.toString())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserEvent not found"));
    }

    private UserEvent normalizeForCreate(UserEvent request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must be positive");
        }
        if (!StringUtils.hasText(request.getEventType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventType is required");
        }

        return UserEvent.builder()
                .id(request.getId() == null ? UUID.randomUUID() : request.getId())
                .userId(request.getUserId())
                .eventType(request.getEventType())
                .eventTime(request.getEventTime() == null ? Instant.now() : request.getEventTime())
                .payload(request.getPayload() == null ? "{}" : request.getPayload())
                .build();
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be positive");
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
