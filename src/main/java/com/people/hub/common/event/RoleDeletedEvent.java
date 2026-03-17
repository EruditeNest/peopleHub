package com.people.hub.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class RoleDeletedEvent {
    private final Set<Long> roleIds;
}
