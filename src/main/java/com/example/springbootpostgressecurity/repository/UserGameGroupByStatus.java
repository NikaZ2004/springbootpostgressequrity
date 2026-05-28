package com.example.springbootpostgressecurity.repository;

import com.example.springbootpostgressecurity.models.game.UserStatus;

public interface UserGameGroupByStatus {
    UserStatus getStatus();
    int getCount();
}
