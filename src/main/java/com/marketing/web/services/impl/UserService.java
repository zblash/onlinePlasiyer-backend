package com.marketing.web.services.impl;

import com.marketing.web.models.Role;
import com.marketing.web.models.User;
import com.marketing.web.repositories.RoleRepository;
import com.marketing.web.repositories.UserRepository;
import com.marketing.web.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CartService cartService;

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(RuntimeException::new);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public User create(User user) {
        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        cartService.create(createdUser);
        return createdUser;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}
