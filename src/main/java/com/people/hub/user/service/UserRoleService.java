package com.people.hub.user.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.security.MyUserDetail;
import com.people.hub.user.model.UserRole;
import com.people.hub.user.repository.UserRoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepo userRoleRepo;

    public List<UserRole> createUserRole(Long userId, Set<Long> roleIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        List<UserRole> userRoles = roleIds.stream()
            .map(roleId -> {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreatedBy(userDetail.getUserId());
                userRole.setUpdatedBy(userDetail.getUserId());
                return userRole;
            })
            .toList();
        return userRoleRepo.saveAll(userRoles);
    }

    @Transactional
    public RestApiResponse updateUserRole(Long userId, Set<Long> roleIds) {
        Set<Long> previousRoleIds = userRoleRepo.findAllRoleIdsByUserId(userId);

        Set<Long> removeRoleIds = new HashSet<>(previousRoleIds);
        removeRoleIds.removeAll(roleIds);

        Set<Long> addRoleIds = new HashSet<>(roleIds);
        addRoleIds.removeAll(previousRoleIds);

        if (!removeRoleIds.isEmpty()) {
            userRoleRepo.deleteAllByUserIdAndRoleIdIn(userId, removeRoleIds);
        }
        if (!addRoleIds.isEmpty()) {
            createUserRole(userId, addRoleIds);
        }

        return RestApiResponse.success();
    }

    public Set<Long> getAllUserIdsByRoleId(Long roleId) {
        return userRoleRepo.findAllByRoleId(roleId).stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toSet());
    }

    public Set<Long> getAllUserIdsByRoleIds(Set<Long> roleIds) {
        return userRoleRepo.findAllByRoleIdIn(roleIds).stream()
            .map(UserRole::getUserId)
            .collect(Collectors.toSet());
    }

    public Set<Long> getAllRoleIdsByUserId(Long userId) {
        return userRoleRepo.findAllRoleIdsByUserId(userId);
    }

    public Set<Long> getAllRoleIdsByUserIds(Set<Long> userIds) {
        return userRoleRepo.findAllByUserIdIn(userIds).stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toSet());
    }

    @Transactional
    public int deleteUserRoleByUserId(Long userId) {
        return userRoleRepo.deleteAllByUserId(userId);
    }

    @Transactional
    public int deleteUserRoleByRoleId(Long roleId) {
        return userRoleRepo.deleteAllByRoleId(roleId);
    }

    @Transactional
    public int deleteUserRolesByUserIds(Set<Long> userIds) {
        return userRoleRepo.deleteAllByUserIdIn(userIds);
    }

    @Transactional
    public int deleteUserRolesByRoleIds(Set<Long> roleIds) {
        return userRoleRepo.deleteAllByRoleIdIn(roleIds);
    }
}
