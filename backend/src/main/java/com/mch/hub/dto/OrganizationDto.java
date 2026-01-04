package com.mch.hub.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrganizationDto(UUID id, String slug, String displayName, String description, OffsetDateTime createdAt, int memberCount) {}
