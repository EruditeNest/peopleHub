package com.people.hub.authorization.service;

import com.people.hub.authorization.model.PermissionGroup;
import com.people.hub.authorization.repository.PermissionGroupRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionGroupService {

    private final PermissionGroupRepo groupRepo;

    public List<PermissionGroup> getAllPermissionGroupsById(Set<Long> groupIds) {
        return groupRepo.findAllById(groupIds);
    }
}
