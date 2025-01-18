package com.redis_integration_demo.service;

import com.redis_integration_demo.dto.CreateSessionRequest;
import com.redis_integration_demo.dto.SessionResponse;
import com.redis_integration_demo.model.Session;
import com.redis_integration_demo.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @CachePut(value = "sessions", key = "#result.userId")
    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        sessionRepository.deleteByUserId(request.getUserId());

        Session session = new Session();
        session.setUserId(request.getUserId());
        session.setPassword(request.getPassword());

        sessionRepository.save(session);

        return new SessionResponse(
                session.getSessionId(),
                session.getUserId(),
                session.getCreatedAt().toString(),
                45 * 60
        );
    }
}
