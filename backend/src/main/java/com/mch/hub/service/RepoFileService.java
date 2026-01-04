package com.mch.hub.service;

import ca.bkaw.mch.Sha1;
import ca.bkaw.mch.object.ObjectStorageTypes;
import ca.bkaw.mch.object.Reference20;
import ca.bkaw.mch.object.commit.Commit;
import ca.bkaw.mch.object.dimension.Dimension;
import ca.bkaw.mch.object.tree.Tree;
import ca.bkaw.mch.object.world.World;
import ca.bkaw.mch.object.worldcontainer.WorldContainer;
import ca.bkaw.mch.repository.MchConfiguration;
import ca.bkaw.mch.repository.MchRepository;
import ca.bkaw.mch.repository.TrackedWorld;
import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.dto.FileInfoDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class RepoFileService {
    public List<FileInfoDto> listFiles(RepositoryEntity repoEntity, String commitHash, String path) throws IOException {
        Sha1 commitSha1 = Sha1.fromString(commitHash);
        MchRepository repo = new MchRepository(Path.of(repoEntity.getStoragePath()));
        repo.readConfiguration();
        Commit commit = ObjectStorageTypes.COMMIT.read(commitSha1, repo);
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (path.isEmpty()) { // root
            return this.listWorldsAsRootFiles(commit, repo);
        }
        if (!path.contains(("/"))) {
            return List.of();
        }
        List<String> parts = Arrays.asList(path.split("/"));
        String worldDirName = parts.removeFirst();
        World world = this.getWorld(worldDirName, commit, repo);
        Dimension overworld = world.getDimension(Dimension.OVERWORLD).resolve(repo);
        Tree miscFiles = overworld.getMiscellaneousFiles().resolve(repo);
        List<FileInfoDto> files = new ArrayList<>();
        for (var entry : miscFiles.getSubTrees().entrySet()) {
            files.add(new FileInfoDto(entry.getKey(), 0, FileInfoDto.FileType.DIRECTORY, ""));
        }
        for (var entry : miscFiles.getFiles().entrySet()) {
            Tree.BlobReference blobReference = entry.getValue();
            String lastModified = Long.toString(blobReference.lastModified());
            // TODO some way of getting the file size would be nice...
            files.add(new FileInfoDto(entry.getKey(), 0, FileInfoDto.FileType.FILE, lastModified));
        }
        return files;
    }

    private List<FileInfoDto> listWorldsAsRootFiles(Commit commit, MchRepository repo) throws IOException {
        WorldContainer worldContainer = commit.getWorldContainer().resolve(repo);
        MchConfiguration config = repo.getConfiguration();
        return worldContainer.getWorlds().entrySet().stream().map(entry -> {
            Sha1 worldId = entry.getKey();
            TrackedWorld trackedWorld = config.getTrackedWorld(worldId);
            String fileName = trackedWorld.getName() + " (" + worldId.asHex().substring(0, 7) + ")";
            return new FileInfoDto(fileName, 0, FileInfoDto.FileType.DIRECTORY, "");
        }).toList();
    }

    private World getWorld(String directoryName, Commit commit, MchRepository repo) throws IOException {
        String sha1Short = directoryName.substring(directoryName.lastIndexOf('(') + 1, directoryName.lastIndexOf(')')).trim();
        WorldContainer worldContainer = commit.getWorldContainer().resolve(repo);
        for (Map.Entry<Sha1, Reference20<World>> entry : worldContainer.getWorlds().entrySet()) {
            if (entry.getKey().asHex().startsWith(sha1Short)) {
                return entry.getValue().resolve(repo);
            }
        }
        throw new IOException("World not found: " + directoryName);
    }

    public void downloadFile(RepositoryEntity repo, String commit, String path, HttpServletResponse response) {
        throw new UnsupportedOperationException("TODO");
    }
}
