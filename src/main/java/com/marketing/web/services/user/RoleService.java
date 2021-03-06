package com.marketing.web.services.user;


import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Role;

import java.util.List;

public interface RoleService {

    List<Role> findAll();

    Role findByName(String name);

    Role create(Role role);

    Role update(Role role, Role updatedRole);

    void delete(Role role);

    Role createOrFind(String roleName);

    RoleType getLoggedInUserRole();
}
