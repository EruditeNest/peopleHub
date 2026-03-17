package com.people.hub.user.service;

import com.people.hub.common.event.RoleDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RoleEventListener {

    private final UserRoleService userRoleService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeleteRole(RoleDeletedEvent event) {
        if (event.getRoleIds() == null || event.getRoleIds().isEmpty()) {
            return;
        }
        userRoleService.deleteUserRolesByRoleIds(event.getRoleIds());
    }
}
