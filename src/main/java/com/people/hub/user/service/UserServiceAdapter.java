package com.people.hub.user.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.service.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Primary
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {
    private final UserService userService;

    @Override
    public RestApiResponse getAllUserByIds(List<Long> userIds, int page, int size, String sortField, String sortOrder) {
        return userService.getAllUserByIds(userIds, page, size, sortField, sortOrder);
    }

    @Override
    public RestApiResponse getAllUserByIds(List<Long> userIds) {
        return userService.getAllUserByIds(userIds);
    }

    @Override
    public boolean activeUserExistsById(Long id){
        return userService.activeUserExistsById(id);
    }

    @Override
    public Set<Long> getAllActiveUserIds(List<Long> ids){
        return userService.getAllActiveUserIds(ids);
    }
}
