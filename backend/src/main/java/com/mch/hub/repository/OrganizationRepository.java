package com.mch.hub.repository;

import com.mch.hub.domain.OrganizationEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {
    @Query("SELECT o FROM OrganizationEntity o LEFT JOIN FETCH o.members WHERE LOWER(o.slug) = LOWER(:slug)")
    Optional<OrganizationEntity> findBySlugIgnoreCase(@Param("slug") String slug);
    
    @Query("SELECT DISTINCT o FROM OrganizationEntity o LEFT JOIN FETCH o.members")
    List<OrganizationEntity> findAll();
}
