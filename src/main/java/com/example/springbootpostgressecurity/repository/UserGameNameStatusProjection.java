package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.game.UserStatus;

public interface UserGameNameStatusProjection {
    String getName();

    UserStatus getStatus();
}
