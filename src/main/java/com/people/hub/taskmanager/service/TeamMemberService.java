package com.people.hub.taskmanager.service;

import com.people.hub.common.exception.NotFoundException;
import com.people.hub.taskmanager.model.TeamMember;
import com.people.hub.taskmanager.repo.TeamMemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepo teamMemberRepo;

    public void addMember(Long teamId, Long memberId) {
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setMemberId(memberId);
        teamMemberRepo.save(teamMember);
    }

    public void addMemberByIds(Long teamId, Set<Long> memberIds) {
        List<TeamMember> teamMemberList = new ArrayList<>();
        for(Long memberId: memberIds) {
            TeamMember teamMember = new TeamMember();
            teamMember.setTeamId(teamId);
            teamMember.setMemberId(memberId);
            teamMemberList.add(teamMember);
        }
        teamMemberRepo.saveAll(teamMemberList);
    }

    public void removeMember(Long teamId, Long memberId) {
        if(teamMemberRepo.existsByTeamIdAndMemberId(teamId, memberId)){
            teamMemberRepo.deleteByTeamIdAndMemberId(teamId, memberId);
        }
        throw new NotFoundException("Member(" + memberId + ") does not exist in the Team(" + teamId + ")");
    }

    public void removeMemberByIds(Long teamId, Set<Long> memberIds) {
        List<Long> existingMemberIds = teamMemberRepo.findAllMemberIdsByTeamIdAndMemberIdsIN(teamId, memberIds);
        Set<Long> existingSet = new HashSet<>(existingMemberIds);

        List<Long> missingMemberIds = memberIds.stream()
                .filter(memberId -> !existingSet.contains(memberId))
                .toList();
        if (!missingMemberIds.isEmpty()) {
            throw new NotFoundException("Members not found for teamId " + teamId + ": " + missingMemberIds);
        }
        teamMemberRepo.deleteByTeamIdAndMemberIdIn(teamId, memberIds);
    }

    public List<Long> getMemberIdsByTeamId(Long teamId) {
        return teamMemberRepo.findMemberIdsByTeamId(teamId);
    }

    @Transactional
    public int deleteAllByTeamId(Long teamId) {
        return teamMemberRepo.deleteAllByTeamId(teamId);
    }
}
