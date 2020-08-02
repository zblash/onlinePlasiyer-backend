package com.marketing.web.services.user;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Role;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.UserRepository;
import com.marketing.web.configs.security.CustomPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleServiceImpl roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleServiceImpl roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }


    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user.name",userName));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user.name", email));
    }

    @Override
    public User findByResetToken(String token) {
        return userRepository.findByPasswordResetToken(token).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user.name", token));
    }

    @Override
    public boolean checkUserByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean canRegister(User user){
        return !userRepository.findByUsernameOrEmailOrName(user.getUsername(), user.getEmail(), user.getName()).isPresent();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByOrderByIdDesc();
    }

    @Override
    public List<User> findAllByRole(RoleType roleType) {
        Role role = roleService.findByName("ROLE_"+roleType.toString());
        return userRepository.findAllByRoleOrderByIdDesc(role);
    }

    @Override
    public List<User> findAllByStatus(boolean status) {
        return userRepository.findAllByStatusOrderByIdDesc(status);
    }

    @Override
    public List<User> findAllByStatesAndRole(List<State> activeStates, RoleType roleType) {
        Role role = roleService.createOrFind("ROLE_"+roleType.toString());
        return userRepository.findAllByStateInAndRoleAndStatus(activeStates, role, true);
    }

    @Override
    public List<User> findAllByRoleAndStatus(RoleType roleType, boolean status) {
        Role role = roleService.createOrFind("ROLE_"+roleType.toString());
        return userRepository.findAllByRoleAndStatusOrderByIdDesc(role, status);
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user",id));
    }


    @Override
    public User create(User user, RoleType roleType) {
        Role role = roleService.createOrFind("ROLE_"+roleType.toString());
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(String id, User updatedUser) {
        User user = findById(id);
        user.setUsername(updatedUser.getUsername());
        user.setName(updatedUser.getName());
        user.setPassword(updatedUser.getPassword());
        user.setEmail(updatedUser.getEmail());
        user.setStatus(updatedUser.isStatus());
        user.setRole(updatedUser.getRole());
        user.setPasswordResetToken(updatedUser.getPasswordResetToken());
        if (updatedUser.getResetTokenExpireTime() != null) {
            user.setResetTokenExpireTime(updatedUser.getResetTokenExpireTime());
        }
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public User getLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomPrincipal) auth.getPrincipal()).getUser();
    }

    @Override
    public User changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return update(user.getId().toString(),user);
    }

    @Override
    public boolean loginControl(String username, String password) {
        User user = findByUserName(username);
       return passwordEncoder.matches(password, user.getPassword()) && user.isStatus();
    }

    @Override
    public User findByActivationToken(String activationToken) {
        return userRepository.findByActivationToken(activationToken).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user", activationToken));
    }

}
