package com.marketing.web.services.user;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.Role;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.repositories.UserRepository;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.services.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CartService cartService;

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new ResourceNotFoundException("User not found with username: "+ userName));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllByRole(RoleType roleType) {
        Role role = roleService.findByName("ROLE_"+roleType.toString());
        return userRepository.findAllByRole(role);
    }

    @Override
    public List<User> findAllByRoleAndStatus(RoleType roleType,boolean status) {
        Role role = roleService.findByName("ROLE_"+roleType.toString());
        return userRepository.findAllByRoleAndStatus(role,status);
    }

    @Override
    public List<User> findAllByStatus(boolean status) {
        return userRepository.findAllByStatus(status);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+ id));
    }

    @Override
    public User findByUUID(String uuid) {
        return userRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+ uuid));
    }

    @Override
    public User create(User user, RoleType roleType) {
        Role role = roleService.createOrFind("ROLE_"+roleType.toString());
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        Cart cart = cartService.create(createdUser);
        user.setCart(cart);
        return createdUser;
    }

    @Override
    public User update(Long id, User updatedUser) {
        User user = findById(id);
        user.setUsername(updatedUser.getUsername());
        user.setName(updatedUser.getName());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        user.setEmail(updatedUser.getEmail());
        user.setTaxNumber(updatedUser.getTaxNumber());
        user.setStatus(updatedUser.isStatus());
        user.setRole(updatedUser.getRole());
        user.setActiveStates(updatedUser.getActiveStates());
        user.setAddress(updatedUser.getAddress());
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    public User getLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomPrincipal) auth.getPrincipal()).getUser();
    }
}
