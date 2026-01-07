package com.mch.hub.service;

import ca.bkaw.mch.Sha1;
import ca.bkaw.mch.object.ObjectStorageTypes;
import ca.bkaw.mch.object.Reference20;
import ca.bkaw.mch.object.commit.Commit;
import ca.bkaw.mch.repository.MchRepository;
import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.dto.CommitDto;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CommitService {

    public List<CommitDto> listCommits(RepositoryEntity repoEntity) throws IOException {
        MchRepository repo = new MchRepository(Path.of(repoEntity.getStoragePath()));
        List<CommitDto> commits = new ArrayList<>();
        Reference20<Commit> ref = repo.getHeadCommit();
        while (ref != null) {
            Commit commit = ref.resolve(repo);
            commits.add(toDto(ref.getSha1(), commit));
            ref = commit.getPreviousCommit();
        }
        return commits;
    }

    public CommitDto getCommit(RepositoryEntity repoEntity, String hash) throws IOException {
        MchRepository repo = new MchRepository(Path.of(repoEntity.getStoragePath()));
        Sha1 sha1 = Sha1.fromString(hash);
        Commit commit = ObjectStorageTypes.COMMIT.read(sha1, repo);
        return toDto(sha1, commit);
    }

    public static CommitDto toDto(Sha1 hash, Commit commit) {
        Reference20<Commit> prev = commit.getPreviousCommit();
        return new CommitDto(
            hash.asHex(),
            commit.getMessage(),
            OffsetDateTime.ofInstant(Instant.ofEpochMilli(commit.getTime()), ZoneId.systemDefault()),
            prev != null ? prev.getSha1().asHex() : null
        );
    }
}
