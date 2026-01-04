package com.mch.hub.service;

import com.mch.hub.domain.OrganizationEntity;
import com.mch.hub.dto.OrganizationDto;
import com.mch.hub.repository.OrganizationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public List<OrganizationDto> findAll() {
        return organizationRepository.findAll().stream()
            .map(OrganizationService::toDto)
            .toList();
    }

    public OrganizationDto getBySlug(String slug) {
        OrganizationEntity org = organizationRepository.findBySlugIgnoreCase(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        return toDto(org);
    }

    public OrganizationEntity getEntityBySlug(String slug) {
        return organizationRepository.findBySlugIgnoreCase(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    public static OrganizationDto toDto(OrganizationEntity entity) {
        int memberCount = entity.getMembers() == null ? 0 : entity.getMembers().size();
        return new OrganizationDto(entity.getId(), entity.getSlug(), entity.getDisplayName(), entity.getDescription(), entity.getCreatedAt(), memberCount);
    }
}
