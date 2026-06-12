package com.people.hub.common.service;

import com.people.hub.common.RestApiResponse;

import java.util.List;
import java.util.Set;

public interface UserServicePort {
    RestApiResponse getAllUserByIds(List<Long> userIds, int page, int size, String sortField, String sortOrder);

    RestApiResponse getAllUserByIds(List<Long> userIds);

    boolean activeUserExistsById(Long id);

    Set<Long> getAllActiveUserIds(List<Long> ids);

    List<Long> getAllUserIds();
}
