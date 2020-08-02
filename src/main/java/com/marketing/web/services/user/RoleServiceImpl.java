package com.marketing.web.services.user;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.configs.security.CustomPrincipal;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Role;
import com.marketing.web.repositories.RoleRepository;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"role", name));
    }

    @Override
    public Role create(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role update(Role role, Role updatedRole) {
        role.setName(updatedRole.getName());
        return roleRepository.save(role);
    }

    @Override
    public void delete(Role role) {
        roleRepository.delete(role);
    }

    public Role createOrFind(String roleName){
        Optional<Role> optionalRole = roleRepository.findByName(roleName);
        if (optionalRole.isPresent()){
            return optionalRole.get();
        }
        Role role = new Role();
        role.setName(roleName);
        return create(role);


    }

    @Override
    public RoleType getLoggedInUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String roleStr = ((CustomPrincipal) auth.getPrincipal()).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findAny().orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user", ""));
        return RoleType.fromValue(roleStr.split("_")[1]);
    }
}
