package com.marketing.web.services.user;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.CreditType;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.Credit;
import com.marketing.web.models.Role;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.User;
import com.marketing.web.repositories.UserRepository;
import com.marketing.web.configs.security.CustomPrincipal;
import com.marketing.web.services.cart.CartServiceImpl;
import com.marketing.web.services.credit.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private CreditService creditService;

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user.name",userName));
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
    public List<User> findAllByRoleAndStatus(RoleType roleType,boolean status) {
        Role role = roleService.findByName("ROLE_"+roleType.toString());
        return userRepository.findAllByRoleAndStatusOrderByIdDesc(role,status);
    }

    @Override
    public List<User> findAllByStatus(boolean status) {
        return userRepository.findAllByStatusOrderByIdDesc(status);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user",""));
    }

    @Override
    public User findByUUID(String uuid) {
        return userRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"user", uuid));
    }

    @Override
    public User create(User user, RoleType roleType) {
        Role role = roleService.createOrFind("ROLE_"+roleType.toString());
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        if (roleType.equals(RoleType.CUSTOMER)) {
            Cart cart = cartService.create(createdUser);
            user.setCart(cart);
            Credit credit = new Credit();
            credit.setCustomer(createdUser);
            credit.setTotalDebt(0);
            credit.setCreditLimit(0);
            credit.setCreditType(CreditType.SCRD);
            creditService.create(credit);
        }
        return createdUser;
    }

    @Override
    public User update(Long id, User updatedUser) {
        User user = findById(id);
        user.setUsername(updatedUser.getUsername());
        user.setName(updatedUser.getName());
        user.setPassword(updatedUser.getPassword());
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

    @Override
    public User getLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomPrincipal) auth.getPrincipal()).getUser();
    }
}
