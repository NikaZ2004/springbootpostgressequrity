package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.game.UserStatus;

public interface UserGameStatusProjection {
    Long getId();

    UserStatus getStatus();
}
