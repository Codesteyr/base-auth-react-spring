package com.codesteyr.controlpanel.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.codesteyr.controlpanel.entity.Users;

import java.util.Optional;


public interface UsersRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findByName(String name);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
}
