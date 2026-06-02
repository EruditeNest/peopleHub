package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface TeamMemberRepo extends JpaRepository<TeamMember, Long> {
    int deleteAllByTeamId(Long teamId);

    boolean existsByTeamIdAndMemberId(Long teamId, Long memberId);

    int deleteByTeamIdAndMemberId(Long teamId, Long memberId);

    @Query("""
       SELECT tm.memberId
       FROM TeamMember tm
       WHERE tm.teamId = :teamId
       """)
    List<Long> findMemberIdsByTeamId(@Param("teamId") Long teamId);

    @Query("""
       SELECT tm.memberId
       FROM TeamMember tm
       WHERE tm.teamId = :teamId
       AND tm.memberId IN :memberIds
       """)
    List<Long> findAllMemberIdsByTeamIdAndMemberIdsIN(
            @Param("teamId") Long teamId,
            @Param("memberIds") Collection<Long> memberIds
    );

    @Transactional
    @Modifying
    int deleteByTeamIdAndMemberIdIn(Long teamId, Collection<Long> uniqueMemberIds);
}
