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

    @GetMapping("/{ownerSlug}/{repoSlug}/commits")
    public List<CommitDto> listUserRepoCommits(@PathVariable String ownerSlug, @PathVariable String repoSlug) throws IOException {
        var repo = repositoryService.getBySlug(ownerSlug, repoSlug);
        return commitService.listCommits(repo);
    }

    @GetMapping("/{ownerSlug}/{repoSlug}/commits/{hash}")
    public CommitDto getUserRepoCommit(@PathVariable String ownerSlug, @PathVariable String repoSlug, @PathVariable String hash) throws IOException {
        var repo = repositoryService.getBySlug(ownerSlug, repoSlug);
        return commitService.getCommit(repo, hash);
    }
}
