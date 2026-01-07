package com.mch.hub.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RepositoryPasswordException extends ResponseStatusException {
    public RepositoryPasswordException() {
        super(HttpStatus.UNAUTHORIZED, "Repository is password protected");
    }
}
