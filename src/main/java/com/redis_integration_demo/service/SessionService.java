package com.redis_integration_demo.service;

import com.redis_integration_demo.dto.CreateSessionRequest;
import com.redis_integration_demo.dto.SessionResponse;
import com.redis_integration_demo.model.Session;
import com.redis_integration_demo.model.User;
import com.redis_integration_demo.repository.SessionRepository;
import com.redis_integration_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisLogsService redisLogsService;

    @CachePut(value = "sessions", key = "#result.userId")
    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        sessionRepository.deleteByUserId(request.getUserId());

        Session session = new Session();
        session.setUserId(request.getUserId());
        session.setPassword(request.getPassword());

        sessionRepository.save(session);

        redisLogsService.addLog(String.format("Session created for userId: %d", request.getUserId()));

        return new SessionResponse(
                session.getSessionId(),
                session.getUserId(),
                session.getCreatedAt().toString(),
                45 * 60
        );
    }

    @Cacheable(value = "sessions", key = "#userId")
    public SessionResponse getSessionByUserId(Long userId) {
        Session session = sessionRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Сессия у данного пользователя не найдена"));

        return new SessionResponse(
                session.getSessionId(),
                session.getUserId(),
                session.getCreatedAt().toString(),
                45 * 60
        );
    }

    @CacheEvict(value = "sessions", key = "#userId")
    @Transactional
    public void deleteSession(Long userId) {
        Session session = sessionRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Сессия у данного пользователя не найдена"));

        sessionRepository.delete(session);

        redisLogsService.addLog(String.format("Session deleted for userId: %d", session.getUserId()));
    }
}
