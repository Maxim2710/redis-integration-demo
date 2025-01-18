package com.redis_integration_demo.controller;

import com.redis_integration_demo.dto.CreateSessionRequest;
import com.redis_integration_demo.dto.ErrorResponse;
import com.redis_integration_demo.dto.SessionResponse;
import com.redis_integration_demo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    public ResponseEntity<Object> createSession(@RequestBody CreateSessionRequest request) {
        try {
            SessionResponse response = sessionService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<Object> getSessionByUserId(@PathVariable Long userId) {
        try {
            SessionResponse response = sessionService.getSessionByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
        }
    }
}
