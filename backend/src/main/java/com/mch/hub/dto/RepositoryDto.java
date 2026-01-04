package com.mch.hub.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RepositoryDto(
    UUID id,
    String name,
    String description,
    String ownerType,
    String ownerName,
    String storagePath,
    OffsetDateTime createdAt
) {}
