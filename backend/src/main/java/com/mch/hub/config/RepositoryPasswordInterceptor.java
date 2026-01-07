package com.mch.hub.config;

import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.error.RepositoryPasswordException;
import com.mch.hub.service.RepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class RepositoryPasswordInterceptor implements HandlerInterceptor {
    private final RepositoryService repositoryService;

    public RepositoryPasswordInterceptor(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Extract path variables
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null || !pathVariables.containsKey("ownerSlug") || !pathVariables.containsKey("repoSlug")) {
            // Not a repository route, continue
            return true;
        }

        String ownerSlug = pathVariables.get("ownerSlug");
        String repoSlug = pathVariables.get("repoSlug");

        // Get the repository
        RepositoryEntity repo;
        try {
            repo = repositoryService.getBySlug(ownerSlug, repoSlug);
        } catch (Exception e) {
            // Repository not found or other error - let the controller handle it
            return true;
        }

        // Check if the repository requires a password
        if (!repo.getVisibility().isPasswordProtected()) {
            // No password required, continue
            return true;
        }

        // Extract password from Authorization header
        String authHeader = request.getHeader("Authorization");
        String password = extractPassword(authHeader);

        // Verify password
        if (password == null || !repositoryService.verifyPassword(repo, password)) {
            throw new RepositoryPasswordException();
        }

        return true;
    }

    /**
     * Extract password from Authorization header.
     * Expected format: "Basic base64(repo:password)" where repo is "ownerSlug/repoSlug"
     *
     * @param authHeader The Authorization header value
     * @return The extracted password, or null if not found or invalid format
     */
    private String extractPassword(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }

        // Check if it's Basic auth
        if (!authHeader.startsWith("Basic ")) {
            return null;
        }

        // Extract the base64 part
        String base64Credentials = authHeader.substring(6).trim();

        try {
            // Decode base64
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            // Expected format: "repo:password" or "ownerSlug/repoSlug:password"
            // We just need the password part after the last colon
            int lastColonIndex = credentials.lastIndexOf(':');
            if (lastColonIndex == -1) {
                return null;
            }

            return credentials.substring(lastColonIndex + 1);
        } catch (IllegalArgumentException e) {
            // Invalid base64
            return null;
        }
    }
}

