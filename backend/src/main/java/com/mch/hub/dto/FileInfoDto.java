package com.mch.hub.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public record FileInfoDto(String name, int size, FileType type, String rawModifiedAt) {
    public enum FileType {
        UNKNOWN(0),
        FILE(1),
        DIRECTORY(2),
        SYMBOLIC_LINK(3),
        ;

        private final int value;

        FileType(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return this.value;
        }
    }
}
