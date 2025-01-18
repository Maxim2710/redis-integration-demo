package com.redis_integration_demo.controller;

import com.redis_integration_demo.dto.ErrorResponse;
import com.redis_integration_demo.dto.LogResponse;
import com.redis_integration_demo.service.RedisLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/logs")
public class RedisLogsController {

    @Autowired
    private RedisLogsService redisLogsService;

    @GetMapping
    public ResponseEntity<Object> getLogs() {
        try {
            List<LogResponse> logs = redisLogsService.getRecentLogs();
            return ResponseEntity.ok(logs);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getMessage()));
        }
    }
}
