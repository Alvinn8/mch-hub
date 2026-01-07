package com.mch.hub.domain;

/**
 * Repository visibility levels.
 */
public enum Visibility {
    /**
     * Only accessible by owner and organization members.
     */
    PRIVATE,

    /**
     * Publicly accessible and listed on the owner's profile.
     */
    PUBLIC,

    /**
     * Publicly accessible but not listed on the owner's profile.
     */
    UNLISTED,

    /**
     * Listed on owner's profile but requires a password to access.
     */
    PUBLIC_PASSWORD,

    /**
     * Not listed on owner's profile and requires a password to access.
     */
    UNLISTED_PASSWORD,

    ;

    public boolean isPasswordProtected() {
        return this == PUBLIC_PASSWORD || this == UNLISTED_PASSWORD;
    }
}

