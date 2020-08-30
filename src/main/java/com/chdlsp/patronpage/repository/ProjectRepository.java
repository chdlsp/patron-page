package com.chdlsp.patronpage.repository;

import com.chdlsp.patronpage.model.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {

    Page<ProjectEntity> findAll(Pageable pageable);
    Optional<ProjectEntity> findByProjectId(UUID projectId);

}