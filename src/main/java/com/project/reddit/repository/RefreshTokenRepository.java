package com.project.reddit.repository;

import com.project.reddit.entities.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface RefreshTokenRepository extends JpaRepository<Subreddit, Long> {

    Optional<Subreddit> findByName(String subredditName);
}