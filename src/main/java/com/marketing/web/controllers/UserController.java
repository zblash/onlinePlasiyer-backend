package com.marketing.web.controllers;

import com.marketing.web.dtos.user.*;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.User;
import com.marketing.web.services.user.*;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{roleType}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String roleType, @RequestParam(required = false) Boolean status) {
        RoleType role = RoleType.valueOf(roleType.toUpperCase());
        List<User> users;

        if (status != null) {
            users = userService.findAllByRoleAndStatus(role, status);
        }else {
            users = userService.findAllByRole(role);
        }

        switch (role) {
            case CUSTOMER:
                return ResponseEntity.ok(users.stream()
                        .map(UserMapper::userToCustomer)
                        .collect(Collectors.toList()));
            case MERCHANT:
                return ResponseEntity.ok(users.stream()
                        .map(UserMapper::userToMerchant)
                        .collect(Collectors.toList()));
            default:
                return ResponseEntity.ok(users.stream()
                        .map(UserMapper::userToAdmin)
                        .collect(Collectors.toList()));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/changeStatus/{id}/{status}")
    public ResponseEntity<?> changeUserStatus(@PathVariable String id,@PathVariable boolean status){
        User user = userService.findByUUID(id);
        user.setStatus(status);
        user = userService.update(user.getId(),user);
        RoleType role = RoleType.valueOf(user.getRole().getName().split("_")[1].toUpperCase());
        switch (role) {
            case CUSTOMER:
                return ResponseEntity.ok(UserMapper.userToCustomer(user));
            case MERCHANT:
                return ResponseEntity.ok(UserMapper.userToMerchant(user));
            default:
                return ResponseEntity.ok(UserMapper.userToAdmin(user));
        }
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/setActive/{id}")
    public ResponseEntity<?> setActiveUser(@PathVariable String id){
        User user = userService.findByUUID(id);
        user.setStatus(true);
        user = userService.update(user.getId(),user);
        RoleType role = RoleType.valueOf(user.getRole().getName().split("_")[1].toUpperCase());
        switch (role) {
            case CUSTOMER:
                return ResponseEntity.ok(UserMapper.userToCustomer(user));
            case MERCHANT:
                return ResponseEntity.ok(UserMapper.userToMerchant(user));
            default:
                return ResponseEntity.ok(UserMapper.userToAdmin(user));
        }
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/setPassive/{id}")
    public ResponseEntity<?> setPassiveUser(@PathVariable String id){
        User user = userService.findByUUID(id);
        user.setStatus(false);
        user = userService.update(user.getId(),user);
        RoleType role = RoleType.valueOf(user.getRole().getName().split("_")[1].toUpperCase());
        switch (role) {
            case CUSTOMER:
                return ResponseEntity.ok(UserMapper.userToCustomer(user));
            case MERCHANT:
                return ResponseEntity.ok(UserMapper.userToMerchant(user));
            default:
                return ResponseEntity.ok(UserMapper.userToAdmin(user));
        }
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerUser>> getAllCustomers(){

        return ResponseEntity.ok(userService.findAllByRole(RoleType.CUSTOMER).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/merchant")
    public ResponseEntity<List<MerchantUser>> getAllMerchants(){
        return ResponseEntity.ok(userService.findAllByRole(RoleType.MERCHANT).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/merchant/passive")
    public ResponseEntity<List<MerchantUser>> getPassiveMerchantUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT,false).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/merchant/active")
    public ResponseEntity<List<MerchantUser>> getActiveMerchantUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT,true).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customers/passive")
    public ResponseEntity<List<CustomerUser>> getPassiveCustomerUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER,false).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customers/active")
    public ResponseEntity<List<CustomerUser>> getActiveCustomerUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER,true).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }
}
