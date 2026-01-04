package com.mch.hub.controller;

import com.mch.hub.dto.FileInfoDto;
import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.service.RepoFileService;
import com.mch.hub.service.RepositoryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/repos")
public class RepositoryController {
    private final RepositoryService repositoryService;
    private final RepoFileService repoFileService;

    public RepositoryController(RepositoryService repositoryService, RepoFileService repoFileService) {
        this.repositoryService = repositoryService;
        this.repoFileService = repoFileService;
    }

    @GetMapping("/users/{username}/{repo}")
    public RepositoryDto getUserRepo(@PathVariable String username, @PathVariable("repo") String repoName) {
        return repositoryService.getByUserAndName(username, repoName);
    }

    @GetMapping("/orgs/{slug}/{repo}")
    public RepositoryDto getOrgRepo(@PathVariable String slug, @PathVariable("repo") String repoName) {
        return repositoryService.getByOrganizationAndName(slug, repoName);
    }

    @GetMapping("/{ownerSlug}/{repoSlug}/files/list")
    public List<FileInfoDto> listFiles(
        @PathVariable String ownerSlug,
        @PathVariable String repoSlug,
        @RequestParam String commit,
        @RequestParam String path
    ) throws IOException {
        var repo = repositoryService.getBySlug(ownerSlug, repoSlug);
        return repoFileService.listFiles(repo, commit, path);
    }

    @GetMapping("/{ownerSlug}/{repoSlug}/files/download" )
    public void downloadFile(
        @PathVariable String ownerSlug,
        @PathVariable String repoSlug,
        @RequestParam String commit,
        @RequestParam String path,
        HttpServletResponse response
    ) throws IOException {
        var repo = repositoryService.getBySlug(ownerSlug, repoSlug);
        repoFileService.downloadFile(repo, commit, path, response);
    }

}
