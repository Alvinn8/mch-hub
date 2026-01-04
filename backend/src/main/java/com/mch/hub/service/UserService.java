package com.mch.hub.service;

import com.mch.hub.domain.UserEntity;
import com.mch.hub.dto.UserDto;
import com.mch.hub.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(UserService::toDto)
            .toList();
    }

    public UserDto getByUsername(String username) {
        UserEntity user = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDto(user);
    }

    public UserEntity getEntityByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserEntity getEntity(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public static UserDto toDto(UserEntity entity) {
        return new UserDto(entity.getId(), entity.getUsername(), entity.getDisplayName(), entity.getEmail(), entity.getCreatedAt());
    }
}
