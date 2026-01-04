package com.mch.hub.controller;

import com.mch.hub.dto.CommitDto;
import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.service.CommitService;
import com.mch.hub.service.RepositoryService;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repos")
public class CommitController {
    private final RepositoryService repositoryService;
    private final CommitService commitService;

    public CommitController(RepositoryService repositoryService, CommitService commitService) {
        this.repositoryService = repositoryService;
        this.commitService = commitService;
    }

    @GetMapping("/users/{username}/{repo}/commits")
    public List<CommitDto> listUserRepoCommits(@PathVariable String username, @PathVariable("repo") String repoName) throws IOException {
        var repo = repositoryService.getEntityByUserAndName(username, repoName);
        return commitService.listCommits(repo);
    }

    @GetMapping("/orgs/{slug}/{repo}/commits")
    public List<CommitDto> listOrgRepoCommits(@PathVariable String slug, @PathVariable("repo") String repoName) throws IOException {
        var repo = repositoryService.getEntityByOrgAndName(slug, repoName);
        return commitService.listCommits(repo);
    }

    @GetMapping("/users/{username}/{repo}/commits/{hash}")
    public CommitDto getUserRepoCommit(@PathVariable String username, @PathVariable("repo") String repoName, @PathVariable String hash) throws IOException {
        var repo = repositoryService.getEntityByUserAndName(username, repoName);
        return commitService.getCommit(repo, hash);
    }

    @GetMapping("/orgs/{slug}/{repo}/commits/{hash}")
    public CommitDto getOrgRepoCommit(@PathVariable String slug, @PathVariable("repo") String repoName, @PathVariable String hash) throws IOException {
        var repo = repositoryService.getEntityByOrgAndName(slug, repoName);
        return commitService.getCommit(repo, hash);
    }

    @GetMapping("/users/{username}/{repo}/commits/{hash}/file-viewer")
    public RepositoryDto fileViewerUserRepo(@PathVariable String username, @PathVariable("repo") String repoName, @PathVariable String hash) {
        // Placeholder; frontend will embed external file manager using this context.
        return repositoryService.getByUserAndName(username, repoName);
    }

    @GetMapping("/orgs/{slug}/{repo}/commits/{hash}/file-viewer")
    public RepositoryDto fileViewerOrgRepo(@PathVariable String slug, @PathVariable("repo") String repoName, @PathVariable String hash) {
        return repositoryService.getByOrganizationAndName(slug, repoName);
    }
}
