package com.mch.hub.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommitDto(String hash, String message, OffsetDateTime committedAt, String previousCommitHash) {}
