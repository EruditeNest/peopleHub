package com.people.hub.common.service;

import java.util.Map;
import java.util.Set;

public interface AuthorityProvider {

    Map<Long, String> getRoleIdNameById(Set<Long> roleIds);

    Map<Long, Set<String>> getPermissionsByRoleId(Set<Long> roleIds);
}
