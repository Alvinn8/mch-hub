package com.mch.hub.service;

import com.mch.hub.domain.OrganizationEntity;
import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.domain.UserEntity;
import com.mch.hub.domain.Visibility;
import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.repository.OrganizationRepository;
import com.mch.hub.repository.RepositoryRepository;
import com.mch.hub.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    public RepositoryService(
        RepositoryRepository repositoryRepository,
        UserRepository userRepository,
        OrganizationRepository organizationRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<RepositoryDto> listByUser(UserEntity owner) {
        return repositoryRepository.findByOwnerUser(owner).stream()
            .filter(repo -> repo.getVisibility() == Visibility.PUBLIC ||
                           repo.getVisibility() == Visibility.PUBLIC_PASSWORD)
            .map(RepositoryService::toDto)
            .toList();
    }

    public List<RepositoryDto> listByOrganization(OrganizationEntity owner) {
        return repositoryRepository.findByOwnerOrganization(owner).stream()
            .filter(repo -> repo.getVisibility() == Visibility.PUBLIC ||
                           repo.getVisibility() == Visibility.PUBLIC_PASSWORD)
            .map(RepositoryService::toDto)
            .toList();
    }

    public RepositoryEntity getEntityByUserAndName(String username, String repoName) {
        return repositoryRepository.findByOwnerUserUsernameIgnoreCaseAndNameIgnoreCase(username, repoName)
            .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
    }

    public RepositoryEntity getEntityByOrgAndName(String slug, String repoName) {
        return repositoryRepository.findByOwnerOrganizationSlugIgnoreCaseAndNameIgnoreCase(slug, repoName)
            .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
    }

    public RepositoryEntity getBySlug(String slug) {
        if (slug == null) {
            throw new IllegalArgumentException("slug must not be null");
        }

        String normalized = slug.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("slug must not be blank");
        }

        // Allow callers to pass full paths like "/owner/repo" or "owner/repo/".
        normalized = normalized.replaceAll("^/+", "");
        normalized = normalized.replaceAll("/+$", "");

        String[] parts = normalized.split("/+");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                "Invalid repository slug. Expected '{owner}/{repo}', got: '" + slug + "'"
            );
        }

        String owner = parts[0];
        String repoName = parts[1];

        return this.getBySlug(owner, repoName);
    }

    public RepositoryEntity getBySlug(String owner, String repoName) {
        if (owner.isBlank() || repoName.isBlank()) {
            throw new IllegalArgumentException(
                "Invalid repository slug. Owner and repo must be non-empty, got: '" + owner + "/" + repoName + "'"
            );
        }

        // Owner identifiers are guaranteed to be unique across users/orgs.
        if (userRepository.findByUsernameIgnoreCase(owner).isPresent()) {
            return getEntityByUserAndName(owner, repoName);
        }
        if (organizationRepository.findBySlugIgnoreCase(owner).isPresent()) {
            return getEntityByOrgAndName(owner, repoName);
        }

        throw new ResourceNotFoundException("Owner not found");
    }

    /**
     * Verify if the provided password matches the repository's password.
     *
     * @param repository The repository to verify against
     * @param password The password to verify
     * @return true if the password is correct, false otherwise
     */
    public boolean verifyPassword(RepositoryEntity repository, String password) {
        if (repository.getPasswordHash() == null) {
            return false;
        }
        return passwordEncoder.matches(password, repository.getPasswordHash());
    }

    public void setPassword(RepositoryEntity repo, String password) {
        String hashed = this.passwordEncoder.encode(password);
        repo.setPasswordHash(hashed);
        repositoryRepository.save(repo);
    }

    public static RepositoryDto toDto(RepositoryEntity entity) {
        String ownerType;
        String ownerName;
        if (entity.getOwnerUser() != null) {
            ownerType = "user";
            ownerName = entity.getOwnerUser().getUsername();
        } else if (entity.getOwnerOrganization() != null) {
            ownerType = "organization";
            ownerName = entity.getOwnerOrganization().getSlug();
        } else {
            ownerType = "unknown";
            ownerName = "";
        }
        return new RepositoryDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            ownerType,
            ownerName,
            entity.getStoragePath(),
            entity.getCreatedAt(),
            entity.getVisibility()
        );
    }
}
