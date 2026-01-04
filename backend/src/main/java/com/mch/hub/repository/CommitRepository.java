package com.mch.hub.repository;

import com.mch.hub.domain.CommitEntity;
import com.mch.hub.domain.RepositoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface CommitRepository extends JpaRepository<CommitEntity, UUID> {
    List<CommitEntity> findByRepositoryOrderByCommittedAtDesc(RepositoryEntity repository);
    Optional<CommitEntity> findByRepositoryAndHash(RepositoryEntity repository, String hash);
}
