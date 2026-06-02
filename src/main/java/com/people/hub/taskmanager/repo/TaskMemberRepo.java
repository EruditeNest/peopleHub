package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.TaskMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskMemberRepo extends JpaRepository<TaskMember, Long> {

    @Modifying
    @Transactional
    int deleteAllByTaskIdAndMemberId(Long taskId, Long memberId);

    @Modifying
    @Transactional
    int deleteAllByTaskIdAndMemberIdIn(Long taskId, List<Long> memberIds);
}
