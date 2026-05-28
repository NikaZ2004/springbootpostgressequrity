package com.example.springbootpostgressecurity.repository;

public interface UserRoleOverviewProjection {
  Long getId();

  String getUsername();

  String getName();

  String getEmail();

  String getEmailDomain();

  Long getRoleCount();

  String getRoles();
}
