package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
    Page<Project> findAllByIdIn(Collection<Long> ids, Pageable pageable);
}
