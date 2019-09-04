package com.marketing.web.services.user;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Role;
import com.marketing.web.repositories.RoleRepository;
import com.marketing.web.services.user.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Role not found with given name: "+ name));
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
}
