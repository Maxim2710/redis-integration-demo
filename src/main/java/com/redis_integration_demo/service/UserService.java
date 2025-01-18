package com.redis_integration_demo.service;

import com.redis_integration_demo.dto.CreateUserRequest;
import com.redis_integration_demo.dto.UpdateUserRequest;
import com.redis_integration_demo.dto.UserListResponse;
import com.redis_integration_demo.dto.UserResponse;
import com.redis_integration_demo.model.User;
import com.redis_integration_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisLogsService redisLogsService;

    public UserResponse createUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userRepository.save(user);

        redisLogsService.addLog(String.format("User created: %s", user.getUsername()));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt().toString()
        );
    }

    // value = "users" -> имя
    // key = "#id" -> как вычисляется ключ кэша (с помощью id)
    // unless = "#result == null" -> результат не будет сохранен в кэш, если он null
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public UserResponse getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }

        User user = userOptional.get();

        redisLogsService.addLog(String.format("Fetched user: %s (ID: %d)", user.getUsername(), user.getId()));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt().toString()
        );
    }

    @CacheEvict(value = "users", key = "#id")
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Пользователь не найден");
        }

        User user = userOptional.get();

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        userRepository.save(user);

        redisLogsService.addLog(String.format("User updated: %s", user.getUsername()));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt().toString()
        );
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Пользователь не найден");
        }

        userRepository.deleteById(id);

        redisLogsService.addLog(String.format("User deleted with ID: %d", id));
    }

    public UserListResponse getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt().toString()
                ))
                .toList();

        return new UserListResponse(users);
    }
}
