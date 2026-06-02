package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TeamRepo extends JpaRepository<Team, Long> {
    Page<Team> findByIdIn(Collection<Long> userIds, Pageable pageable);
}
