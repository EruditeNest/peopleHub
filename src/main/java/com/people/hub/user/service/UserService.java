package com.people.hub.user.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.ForbiddenException;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.user.dto.UserDto;
import com.people.hub.user.model.User;
import com.people.hub.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepo userRepo;
    private final UserRoleService userRoleService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "userId",
        "username",
        "email",
        "isDeleted",
        "isActive",
        "created_at",
        "updated_at"
    );

    public User createUser(UserDto userDto) {
        if(!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new IllegalArgumentException("password and confirmPassword do not match");
        }
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setActive(true);
        user.setDeleted(false);
        user.setDesignation(userDto.getDesignation());
        User newUser = userRepo.save(user);
        userRoleService.createUserRole(newUser.getUserId(), userDto.getRoleIds());
        return newUser;
    }

    public User updateUser(Long userId, UserDto userDto) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException("User", userId));

        if(!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new IllegalArgumentException("password and confirmPassword do not match");
        }

        if(user.isDeleted()){
            throw new ForbiddenException("Deleted user can not be updated...!!!");
        }

        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setActive(userDto.isActive());
        user.setDeleted(false);
        user.setDesignation(userDto.getDesignation());
        userRoleService.updateUserRole(userId, userDto.getRoleIds());
        return user;
    }

    public List<Long> getAllUserIds() {
        return userRepo.findAll().stream()
                .map(User::getUserId)
                .toList();
    }

    public RestApiResponse getAllUsers(int page, int size, String sortField, String sortOrder) {
        log.info("getAllUsers with page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<User> roles = userRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(roles.getNumber(), roles.getSize(), roles.getTotalElements());
        return RestApiResponse.success(pageInfo, roles.getContent());
    }

    public RestApiResponse getAllUsersByRoleIds(
        int page,
        int size,
        String sortField,
        String sortOrder,
        Set<Long> roleIds
    ) {
        log.info("getAllUsers with page: {}, size: {}, sortField: {}, sortOrder: {}, roleIds: {}", page, size, sortField, sortOrder, roleIds);

        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Set<Long> userIds = userRoleService.getAllUserIdsByRoleIds(roleIds);

        Page<User> users = userRepo.findByIdIn(userIds, pageable);
        PageInfo pageInfo = new PageInfo(users.getNumber(), users.getSize(), users.getTotalElements());
        return RestApiResponse.success(pageInfo, users.getContent());
    }

    public RestApiResponse getUserById(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException("User", userId));
        return RestApiResponse.success(user);
    }

    public RestApiResponse getAllUserByIds(List<Long> userIds, int page, int size, String sortField, String sortOrder) {
        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<User> users = userRepo.findByIdIn(userIds, pageable);
        PageInfo pageInfo = new PageInfo(users.getNumber(), users.getSize(), users.getTotalElements());
        return RestApiResponse.success(pageInfo, users.getContent());
    }

    public RestApiResponse getAllUserByIds(List<Long> userIds) {
        List<User> users = userRepo.findAllById(userIds);
        return RestApiResponse.success(users);
    }

    public RestApiResponse getUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", username));
        return RestApiResponse.success(user);
    }
    public RestApiResponse getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User", email));
        return RestApiResponse.success(user);
    }

    @Transactional
    public RestApiResponse deleteUser(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException("User", userId));
        user.setDeleted(true);
        userRepo.save(user);
        userRoleService.deleteUserRoleByUserId(userId);
        return RestApiResponse.success();
    }

    public boolean activeUserExistsById(Long id){
        return userRepo.existsByIdAndDeletedFalseAndActiveTrue(id);
    }

    public Set<Long> getAllActiveUserIds(List<Long> ids){
        return userRepo.getAllActiveUserIds(ids);
    }
}
