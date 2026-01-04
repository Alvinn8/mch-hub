package com.mch.hub.controller;

import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.service.RepositoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repos")
public class RepositoryController {
    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/users/{username}/{repo}")
    public RepositoryDto getUserRepo(@PathVariable String username, @PathVariable("repo") String repoName) {
        return repositoryService.getByUserAndName(username, repoName);
    }

    @GetMapping("/orgs/{slug}/{repo}")
    public RepositoryDto getOrgRepo(@PathVariable String slug, @PathVariable("repo") String repoName) {
        return repositoryService.getByOrganizationAndName(slug, repoName);
    }
}
