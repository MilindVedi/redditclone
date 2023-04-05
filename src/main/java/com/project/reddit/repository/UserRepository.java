package com.project.reddit.repository;

import com.project.reddit.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    Users findByEmail(String email);

    @Query("SELECT u FROM Users u where u.username LIKE CONCAT('%',:search,'%')")
    List<Users> findAllByUserName(String search);


}
