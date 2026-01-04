package com.mch.hub.service;

import com.mch.hub.domain.OrganizationEntity;
import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.domain.UserEntity;
import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.repository.RepositoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {
    private final RepositoryRepository repositoryRepository;

    public RepositoryService(RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    public List<RepositoryDto> findByUser(UserEntity owner) {
        return repositoryRepository.findByOwnerUser(owner).stream()
            .map(RepositoryService::toDto)
            .toList();
    }

    public List<RepositoryDto> findByOrganization(OrganizationEntity owner) {
        return repositoryRepository.findByOwnerOrganization(owner).stream()
            .map(RepositoryService::toDto)
            .toList();
    }

    public RepositoryDto getByUserAndName(String username, String repoName) {
        RepositoryEntity repo = repositoryRepository.findByOwnerUserUsernameIgnoreCaseAndNameIgnoreCase(username, repoName)
            .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
        return toDto(repo);
    }

    public RepositoryDto getByOrganizationAndName(String slug, String repoName) {
        RepositoryEntity repo = repositoryRepository.findByOwnerOrganizationSlugIgnoreCaseAndNameIgnoreCase(slug, repoName)
            .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
        return toDto(repo);
    }

    public RepositoryEntity getEntityByUserAndName(String username, String repoName) {
        return repositoryRepository.findByOwnerUserUsernameIgnoreCaseAndNameIgnoreCase(username, repoName)
            .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
    }

    public RepositoryEntity getEntityByOrgAndName(String slug, String repoName) {
        return repositoryRepository.findByOwnerOrganizationSlugIgnoreCaseAndNameIgnoreCase(slug, repoName)
            .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
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
        return new RepositoryDto(entity.getId(), entity.getName(), entity.getDescription(), ownerType, ownerName, entity.getStoragePath(), entity.getCreatedAt());
    }
}
