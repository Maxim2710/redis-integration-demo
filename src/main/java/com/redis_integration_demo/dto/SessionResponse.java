package com.redis_integration_demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {
    private Long sessionId;
    private Long userId;
    private String createdAt;
    private Integer ttl;
}
