package com.redis_integration_demo.repository;

import com.redis_integration_demo.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteByUserId(Long userId);
}
