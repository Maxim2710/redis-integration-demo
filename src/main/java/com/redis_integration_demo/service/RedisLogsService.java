package com.redis_integration_demo.service;

import com.redis_integration_demo.dto.LogResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedisLogsService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOG_QUEUE = "user_logs";

    public void addLog(String message) {
        redisTemplate.opsForList().leftPush(LOG_QUEUE, message);
        Long size = redisTemplate.opsForList().size(LOG_QUEUE);
        if (size != null && size > 10) {
            redisTemplate.opsForList().rightPop(LOG_QUEUE);
        }
    }

    public List<LogResponse> getRecentLogs() {
        List<String> logs = redisTemplate.opsForList().range(LOG_QUEUE, 0, 9);
        return logs != null ? logs.stream().map(LogResponse::new).collect(Collectors.toList()) : List.of();
    }
}
