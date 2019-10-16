package com.marketing.web.controllers;

import com.marketing.web.dtos.user.*;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Address;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.user.*;
import com.marketing.web.utils.mappers.CityMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private SimpMessagingTemplate webSocket;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody WritableLogin writableLogin){
        User userDetails = userService.findByUserName(writableLogin.getUsername());

        if (userDetails.isStatus() && passwordEncoder.matches(writableLogin.getPassword(),userDetails.getPassword())){
            String jwt= JWTGenerator.generate(userDetails);
            ReadableLogin.LoginDTOBuilder loginDTOBuilder = new ReadableLogin.LoginDTOBuilder(jwt);
            loginDTOBuilder.email(userDetails.getEmail());
            loginDTOBuilder.name(userDetails.getName());
            loginDTOBuilder.userName(userDetails.getUsername());
            String role = userDetails.getRole().getName().split("_")[1];
            loginDTOBuilder.role(role);
            loginDTOBuilder.address(userDetails.getAddress());
            loginDTOBuilder.activeStates(userDetails.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
            ReadableLogin readableLogin = loginDTOBuilder
                    .build();
            return ResponseEntity.ok(readableLogin);
        }

        return new ResponseEntity<>("Given username or password incorrect", HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/sign-up")
    public ResponseEntity<ReadableRegister> signUp(@Valid @RequestBody WritableRegister writableRegister){
        User user = UserMapper.writableRegisterToUser(writableRegister);
        if(userService.canRegister(user)) {

            Address address = new Address();
            City city = cityService.findByUuid(writableRegister.getCityId());
            address.setCity(city);
            address.setState(stateService.findByUuidAndCity(writableRegister.getStateId(),city));
            address.setDetails(writableRegister.getDetails());

            user.setStatus(true);
            user.setAddress(addressService.create(address));
            ReadableRegister readableRegister = UserMapper.userToReadableRegister(userService.create(user, writableRegister.getRoleType()));
            return ResponseEntity.ok(readableRegister);
        }
        throw new BadRequestException("Username or email already registered");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/api/users/addActiveState")
    public ResponseEntity<List<ReadableState>> addActiveState(@RequestBody List<String> states){
        User user = userService.getLoggedInUser();
        List<State> stateList = stateService.findAllByUuids(states);
        List<State> addedList = user.getActiveStates();
        addedList.addAll(stateList);
        user.setActiveStates(addedList.stream().distinct().collect(Collectors.toList()));
        userService.update(user.getId(),user);
        return ResponseEntity.ok(addedList.stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/api/users/activeStates")
    public ResponseEntity<List<ReadableState>> getActiveStates(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(user.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/api/users/getMyInfos")
    public ResponseEntity<UserInfo> getUserInfos(){
        User user = userService.getLoggedInUser();
        UserInfo.Builder userInfoBuilder = new UserInfo.Builder(user.getUsername());
        userInfoBuilder.id(user.getUuid().toString());
        userInfoBuilder.email(user.getEmail());
        userInfoBuilder.name(user.getName());
        String role = user.getRole().getName().split("_")[1];
        userInfoBuilder.role(role);
        userInfoBuilder.address(user.getAddress());
        userInfoBuilder.activeStates(user.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
        UserInfo userInfo = userInfoBuilder
                .build();
        return ResponseEntity.ok(userInfo);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/customers")
    public ResponseEntity<List<CustomerUser>> getAllCustomers(){

        return ResponseEntity.ok(userService.findAllByRole(RoleType.CUSTOMER).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/merchant")
    public ResponseEntity<List<MerchantUser>> getAllMerchants(){
        return ResponseEntity.ok(userService.findAllByRole(RoleType.MERCHANT).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/merchant/passive")
    public ResponseEntity<List<MerchantUser>> getPassiveMerchantUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT,false).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/merchant/active")
    public ResponseEntity<List<MerchantUser>> getActiveMerchantUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT,true).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/customers/passive")
    public ResponseEntity<List<CustomerUser>> getPassiveCustomerUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER,false).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/customers/active")
    public ResponseEntity<List<CustomerUser>> getActiveCustomerUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER,true).stream()
                                .map(UserMapper::userToCustomer)
                                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/{roleType}")
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
    @PostMapping("/api/users/setActive/{id}")
    public ResponseEntity<String> setActiveUser(@PathVariable String id){
        User user = userService.findByUUID(id);
        user.setStatus(true);
        userService.update(user.getId(),user);
        return ResponseEntity.ok("Changed user status");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/api/users/setPassive/{id}")
    public ResponseEntity<String> setPassiveUser(@PathVariable String id){
        User user = userService.findByUUID(id);
        user.setStatus(false);
        userService.update(user.getId(),user);
        return ResponseEntity.ok("Changed user status");
    }
}
