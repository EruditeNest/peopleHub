package com.people.hub.authorization.service;

import com.people.hub.authorization.model.PermissionGroup;
import com.people.hub.authorization.repository.PermissionGroupRepo;
import com.people.hub.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionGroupService {

    private final PermissionGroupRepo groupRepo;
    private final PermissionService permissionService;

    public PermissionGroup createPermissionGroup(String name, String description) {
        PermissionGroup permissionGroup = new PermissionGroup();
        permissionGroup.setName(name);
        permissionGroup.setDescription(description);
        return groupRepo.save(permissionGroup);
    }

    public PermissionGroup updatePermissionGroup(Long id, String name, String description) {
        PermissionGroup permissionGroup = groupRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Permission Group", id));
        permissionGroup.setName(name);
        permissionGroup.setDescription(description);
        return groupRepo.save(permissionGroup);
    }

    public Set<PermissionGroup> getAllPermissionGroup() {
        return new HashSet<>(groupRepo.findAll());
    }

    @Transactional
    public void deletePermissionGroup(Long id) {
        permissionService.deleteAllPermissionsByPermissionGroupId(id);
        groupRepo.deleteById(id);
    }

    public void deletePermissionGroupsById(Set<Long> ids) {
        permissionService.deleteAllPermissionsByPermissionGroupIds(ids);
        groupRepo.deleteAllById(ids);
    }

    public List<PermissionGroup> getAllPermissionGroupsById(Set<Long> groupIds) {
        return groupRepo.findAllById(groupIds);
    }
}
