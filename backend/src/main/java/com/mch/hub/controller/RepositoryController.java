package com.mch.hub.controller;

import com.mch.hub.dto.FileInfoDto;
import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.service.RepoFileService;
import com.mch.hub.service.RepositoryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/{ownerSlug}/{repoSlug}")
    public RepositoryDto getUserRepo(@PathVariable String ownerSlug, @PathVariable String repoSlug) {
        return RepositoryService.toDto(repositoryService.getBySlug(ownerSlug, repoSlug));
    }

    @PostMapping("/{ownerSlug}/{repoSlug}/set-password")
    public void setRepositoryPassword(
        @PathVariable String ownerSlug,
        @PathVariable String repoSlug,
        @RequestParam String password
    ) {
        var repo = repositoryService.getBySlug(ownerSlug, repoSlug);
        repositoryService.setPassword(repo, password);
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
