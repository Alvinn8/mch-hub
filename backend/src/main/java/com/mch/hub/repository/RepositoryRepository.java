package com.mch.hub.repository;

import com.mch.hub.domain.OrganizationEntity;
import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.domain.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositoryRepository extends JpaRepository<RepositoryEntity, UUID> {
    @Query("SELECT r FROM RepositoryEntity r LEFT JOIN FETCH r.ownerUser WHERE r.ownerUser = :ownerUser")
    List<RepositoryEntity> findByOwnerUser(@Param("ownerUser") UserEntity ownerUser);
    
    @Query("SELECT r FROM RepositoryEntity r LEFT JOIN FETCH r.ownerOrganization WHERE r.ownerOrganization = :ownerOrganization")
    List<RepositoryEntity> findByOwnerOrganization(@Param("ownerOrganization") OrganizationEntity ownerOrganization);
    
    @Query("SELECT r FROM RepositoryEntity r LEFT JOIN FETCH r.ownerUser u WHERE LOWER(u.username) = LOWER(:username) AND LOWER(r.name) = LOWER(:name)")
    Optional<RepositoryEntity> findByOwnerUserUsernameIgnoreCaseAndNameIgnoreCase(@Param("username") String username, @Param("name") String name);
    
    @Query("SELECT r FROM RepositoryEntity r LEFT JOIN FETCH r.ownerOrganization o WHERE LOWER(o.slug) = LOWER(:slug) AND LOWER(r.name) = LOWER(:name)")
    Optional<RepositoryEntity> findByOwnerOrganizationSlugIgnoreCaseAndNameIgnoreCase(@Param("slug") String slug, @Param("name") String name);
}
