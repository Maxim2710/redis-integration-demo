package com.redis_integration_demo.repository;

import com.redis_integration_demo.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteByUserId(Long userId);

    Optional<Session> findByUserId(Long userId);
}
