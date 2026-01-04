package com.mch.hub.controller;

import com.mch.hub.dto.OrganizationDto;
import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.service.OrganizationService;
import com.mch.hub.service.RepositoryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orgs")
public class OrganizationController {
    private final OrganizationService organizationService;
    private final RepositoryService repositoryService;

    public OrganizationController(OrganizationService organizationService, RepositoryService repositoryService) {
        this.organizationService = organizationService;
        this.repositoryService = repositoryService;
    }

    @GetMapping
    public List<OrganizationDto> listOrganizations() {
        return organizationService.findAll();
    }

    @GetMapping("/{slug}")
    public OrganizationDto getOrganization(@PathVariable String slug) {
        return organizationService.getBySlug(slug);
    }

    @GetMapping("/{slug}/repos")
    public List<RepositoryDto> listOrganizationRepos(@PathVariable String slug) {
        var org = organizationService.getEntityBySlug(slug);
        return repositoryService.findByOrganization(org);
    }
}
