package com.mch.hub.service;

import ca.bkaw.mch.Sha1;
import ca.bkaw.mch.object.ObjectStorageTypes;
import ca.bkaw.mch.object.Reference20;
import ca.bkaw.mch.object.blob.Blob;
import ca.bkaw.mch.object.commit.Commit;
import ca.bkaw.mch.object.dimension.Dimension;
import ca.bkaw.mch.object.tree.Tree;
import ca.bkaw.mch.object.world.World;
import ca.bkaw.mch.object.worldcontainer.WorldContainer;
import ca.bkaw.mch.region.RegionStorageVisitor;
import ca.bkaw.mch.repository.MchConfiguration;
import ca.bkaw.mch.repository.MchRepository;
import ca.bkaw.mch.repository.TrackedWorld;
import ca.bkaw.mch.util.Util;
import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.dto.FileInfoDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        List<String> parts = new ArrayList<>(Arrays.asList(path.split("/")));
        String worldDirName = parts.removeFirst();
        World world = this.getWorld(worldDirName, commit, repo);
        if (parts.isEmpty()) { // world root
            return this.worldDirectory(world, repo);
        } else if (parts.getFirst().equals(Util.NETHER_FOLDER)) {
            parts.removeFirst();
            return this.dimension(world.getDimension(Dimension.NETHER), repo, parts);
        } else if (parts.getFirst().equals(Util.THE_END_FOLDER)) {
            parts.removeFirst();
            return this.dimension(world.getDimension(Dimension.THE_END), repo, parts);
        } else if (parts.getFirst().equals("dimensions")) {
            return List.of(); // TODO
        } else {
            return this.dimension(world.getDimension(Dimension.OVERWORLD), repo, parts);
        }
    }

    private List<FileInfoDto> listWorldsAsRootFiles(Commit commit, MchRepository repo) throws IOException {
        WorldContainer worldContainer = commit.getWorldContainer().resolve(repo);
        MchConfiguration config = repo.getConfiguration();
        return worldContainer.getWorlds().entrySet().stream().map(entry -> {
            Sha1 worldId = entry.getKey();
            TrackedWorld trackedWorld = config.getTrackedWorld(worldId);
            String fileName = trackedWorld.getName() + " (" + worldId.asHex().substring(0, 7) + ")";
            return new FileInfoDto(fileName, -1, FileInfoDto.FileType.DIRECTORY, "");
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

    private TrackedWorld getTrackedWorld(String directoryName, MchRepository repo) throws IOException {
        String sha1Short = directoryName.substring(directoryName.lastIndexOf('(') + 1, directoryName.lastIndexOf(')')).trim();
        MchConfiguration config = repo.getConfiguration();
        for (TrackedWorld trackedWorld : config.getTrackedWorlds()) {
            if (trackedWorld.getId().asHex().startsWith(sha1Short)) {
                return trackedWorld;
            }
        }
        throw new IOException("Tracked world not found: " + directoryName);
    }

    private void addFileTree(List<FileInfoDto> files, Tree tree) {
        for (var entry : tree.getSubTrees().entrySet()) {
            String dirName = entry.getKey();
            files.add(new FileInfoDto(dirName, -1, FileInfoDto.FileType.DIRECTORY, ""));
        }
        for (var entry : tree.getFiles().entrySet()) {
            String fileName = entry.getKey();
            Tree.BlobReference blobReference = entry.getValue();
            String lastModified = Long.toString(blobReference.lastModified());
            // TODO some way of getting the file size would be nice...
            files.add(new FileInfoDto(fileName, -1, FileInfoDto.FileType.FILE, lastModified));
        }
    }

    private List<FileInfoDto> listRegionFiles(List<Dimension.RegionFileReference> regionFiles) throws IOException {
        List<FileInfoDto> files = new ArrayList<>();
        for (Dimension.RegionFileReference regionFile : regionFiles) {
            String fileName = "r." + regionFile.getRegionX() + "." + regionFile.getRegionZ() + ".mca";
            String lastModified = Long.toString(regionFile.getLastModifiedTime());
            // TODO some way of getting the file size would be nice...
            files.add(new FileInfoDto(fileName, -1, FileInfoDto.FileType.FILE, lastModified));
        }
        return files;
    }

    private List<FileInfoDto> worldDirectory(World world, MchRepository repo) throws IOException {
        List<FileInfoDto> files = new ArrayList<>();
        if (world.getDimensions().containsKey(Dimension.NETHER)) {
            files.add(new FileInfoDto(Util.NETHER_FOLDER, -1, FileInfoDto.FileType.DIRECTORY, ""));
        }
        if (world.getDimensions().containsKey(Dimension.THE_END)) {
            files.add(new FileInfoDto(Util.THE_END_FOLDER, -1, FileInfoDto.FileType.DIRECTORY, ""));
        }
        boolean hasCustomDimensions = world.getDimensions().keySet().stream()
            .anyMatch(dimension -> !Dimension.OVERWORLD.equals(dimension) && !Dimension.NETHER.equals(dimension) && !Dimension.THE_END.equals(dimension));
        if (hasCustomDimensions) {
            files.add(new FileInfoDto("dimensions", -1, FileInfoDto.FileType.DIRECTORY, ""));
        }
        // Miscellaneous files
        Reference20<Dimension> overworldRef = world.getDimension(Dimension.OVERWORLD);
        if (overworldRef != null) {
            files.add(new FileInfoDto("region", -1, FileInfoDto.FileType.DIRECTORY, ""));
            Dimension overworld = overworldRef.resolve(repo);
            Tree miscFiles = overworld.getMiscellaneousFiles().resolve(repo);
            this.addFileTree(files, miscFiles);
        }
        return files;
    }

    private List<FileInfoDto> dimension(Reference20<Dimension> dimensionRef, MchRepository repo, List<String> path) throws IOException {
        if (dimensionRef == null) {
            throw new IOException("ENOENT Dimension not found");
        }
        Dimension dimension = dimensionRef.resolve(repo);
        if (path.isEmpty()) {
            List<FileInfoDto> files = new ArrayList<>();
            files.add(new FileInfoDto("region", -1, FileInfoDto.FileType.DIRECTORY, ""));
            // Miscellaneous files
            Tree miscFiles = dimension.getMiscellaneousFiles().resolve(repo);
            this.addFileTree(files, miscFiles);
            return files;
        }
        if (path.getFirst().equals("region")) {
            path.removeFirst();
            return this.listRegionFiles(dimension.getRegionFiles());
        }
        Tree miscFiles = dimension.getMiscellaneousFiles().resolve(repo);
        return this.traverseTree(miscFiles, repo, path);
    }

    private List<FileInfoDto> traverseTree(Tree tree, MchRepository repo, List<String> path) throws IOException {
        if (path.isEmpty()) {
            List<FileInfoDto> files = new ArrayList<>();
            this.addFileTree(files, tree);
            return files;
        }
        String nextPart = path.removeFirst();
        Reference20<Tree> subTreeRef = tree.getSubTrees().get(nextPart);
        if (subTreeRef == null) {
            throw new IOException("ENOENT Directory not found: " + nextPart);
        }
        Tree subTree = subTreeRef.resolve(repo);
        return this.traverseTree(subTree, repo, path);
    }

    public void downloadFile(RepositoryEntity repoEntity, String commitHash, String path, HttpServletResponse response) throws IOException {
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

        if (path.isEmpty()) {
            throw new IOException("ENOTDIR Cannot download root directory");
        }

        List<String> parts = new ArrayList<>(Arrays.asList(path.split("/")));
        String worldDirName = parts.removeFirst();
        TrackedWorld trackedWorld = this.getTrackedWorld(worldDirName, repo);
        World world = this.getWorld(worldDirName, commit, repo);

        // Extract filename for content disposition
        String fileName = parts.isEmpty() ? worldDirName : parts.get(parts.size() - 1);

        if (parts.isEmpty()) {
            throw new IOException("Cannot download world directory");
        }

        // Navigate to the correct dimension
        String dimensionKey;
        Reference20<Dimension> dimensionRef;
        if (parts.getFirst().equals(Util.NETHER_FOLDER)) {
            parts.removeFirst();
            dimensionKey = Dimension.NETHER;
        } else if (parts.getFirst().equals(Util.THE_END_FOLDER)) {
            parts.removeFirst();
            dimensionKey = Dimension.THE_END;
        } else if (parts.getFirst().equals("dimensions")) {
            throw new IOException("Custom dimensions not yet supported");
        } else {
            dimensionKey = Dimension.OVERWORLD;
        }
        dimensionRef = world.getDimension(dimensionKey);
        if (dimensionRef == null) {
            throw new IOException("Dimension not found: " + dimensionKey);
        }

        Dimension dimension = dimensionRef.resolve(repo);

        // Check if it's a region file
        if (!parts.isEmpty() && parts.getFirst().equals("region")) {
            if (true) {
                throw new UnsupportedOperationException("Not implemented.");
            }
            parts.removeFirst();
            if (parts.isEmpty()) {
                throw new IOException("Cannot download region directory");
            }
            String regionFileName = parts.removeFirst();
            if (!parts.isEmpty()) {
                throw new IOException("Invalid path for region file");
            }

            // Parse region coordinates from filename (format: r.x.z.mca)
            if (!regionFileName.startsWith("r.") || !regionFileName.endsWith(".mca")) {
                throw new IOException("Invalid region file name format");
            }
            String[] regionParts = regionFileName.substring(2, regionFileName.length() - 4).split("\\.");
            if (regionParts.length != 2) {
                throw new IOException("Invalid region file name format");
            }
            int regionX = Integer.parseInt(regionParts[0]);
            int regionZ = Integer.parseInt(regionParts[1]);

            // Find the region file reference
            Dimension.RegionFileReference regionFileRef = null;
            for (Dimension.RegionFileReference ref : dimension.getRegionFiles()) {
                if (ref.getRegionX() == regionX && ref.getRegionZ() == regionZ) {
                    regionFileRef = ref;
                    break;
                }
            }

            if (regionFileRef == null) {
                throw new IOException("Region file not found: " + regionFileName);
            }

            // Read and write the region file
            RegionStorageVisitor.visitReadOnly(repo, trackedWorld, dimensionKey, regionX, regionZ, (chunk) -> {
            });
            throw new UnsupportedOperationException("Not implemented.");
        }

        // It's a miscellaneous file in a tree
        Tree miscFiles = dimension.getMiscellaneousFiles().resolve(repo);
        Tree.BlobReference blobRef = this.findFileInTree(miscFiles, repo, parts);

        if (blobRef == null) {
            throw new IOException("File not found: " + path);
        }

        // Read the blob and write to response
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        Blob blob = blobRef.reference().resolve(repo);
        try (InputStream in = new ByteArrayInputStream(blob.getBytes());
             OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        }
        // try (InputStream in = ObjectStorageTypes.BLOB.readRaw(blobRef.getSha1(), repo);
        //      OutputStream out = response.getOutputStream()) {
        //     in.transferTo(out);
        // }
    }

    private Tree.BlobReference findFileInTree(Tree tree, MchRepository repo, List<String> path) throws IOException {
        if (path.isEmpty()) {
            throw new IOException("Path points to a directory, not a file");
        }

        if (path.size() == 1) {
            // This is the file we're looking for
            return tree.getFiles().get(path.getFirst());
        }

        // Navigate deeper into the tree
        String nextDir = path.removeFirst();
        Reference20<Tree> subTreeRef = tree.getSubTrees().get(nextDir);
        if (subTreeRef == null) {
            throw new IOException("Directory not found: " + nextDir);
        }
        Tree subTree = subTreeRef.resolve(repo);
        return this.findFileInTree(subTree, repo, path);
    }
}
