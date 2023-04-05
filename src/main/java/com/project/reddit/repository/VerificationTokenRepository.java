package com.project.reddit.repository;

import com.project.reddit.entities.VerficationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerficationToken, Long> {
    Optional<VerficationToken> findByToken(String token);


}
