package com.mch.hub.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDto(UUID id, String username, String displayName, String email, OffsetDateTime createdAt) {}
