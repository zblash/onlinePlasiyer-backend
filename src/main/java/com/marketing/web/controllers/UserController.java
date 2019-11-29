package com.marketing.web.controllers;

import com.marketing.web.dtos.user.*;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.HttpMessage;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
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
    public ResponseEntity<?> login(@RequestBody WritableLogin writableLogin, WebRequest request){
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
        HttpMessage httpMessage = new HttpMessage();
        httpMessage.setError("");
        httpMessage.setMessage("Given username or password incorrect");
        httpMessage.setPath(((ServletWebRequest)request).getRequest().getRequestURL().toString());
        httpMessage.setTimestamp(new Date());
        httpMessage.setStatus(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(httpMessage, HttpStatus.UNAUTHORIZED);

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

    @PostMapping("/api/users/changePassword")
    public ResponseEntity<HttpMessage> changeUserPassword(@Valid @RequestBody WritablePasswordReset writablePasswordReset, WebRequest request){
        User user = userService.getLoggedInUser();

        if(writablePasswordReset.getPassword().equals(writablePasswordReset.getPasswordConfirmation())){
            user.setPassword(passwordEncoder.encode(writablePasswordReset.getPassword()));
            userService.update(user.getId(),user);
            HttpMessage httpMessage = new HttpMessage();
            httpMessage.setError("");
            httpMessage.setMessage("Password changed");
            httpMessage.setPath(((ServletWebRequest)request).getRequest().getRequestURL().toString());
            httpMessage.setTimestamp(new Date());
            httpMessage.setStatus(HttpStatus.OK.value());
            return ResponseEntity.ok(httpMessage);
        }
        throw new BadRequestException("Fields not matching");
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
    public ResponseEntity<ReadableUserInfo> getUserInfos(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
    }

    @PostMapping("/api/users/updateInfos")
    public ResponseEntity<ReadableUserInfo> updateUserInfo(@Valid @RequestBody WritableUserInfo writableUserInfo){
        User user = userService.getLoggedInUser();
        if (userService.checkUserByEmail(writableUserInfo.getEmail())){
            user.setName(writableUserInfo.getName());
            user.setEmail(writableUserInfo.getEmail());
            Address address = user.getAddress();
            State state = stateService.findByUuid(writableUserInfo.getAddress().getStateId());
            City city = cityService.findByUuid(writableUserInfo.getAddress().getCityId());
            address.setState(state);
            address.setCity(city);
            address.setDetails(writableUserInfo.getAddress().getDetails());
            user.setAddress(addressService.update(address.getId(),address));
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId(),user)));
        }
        throw new BadRequestException("Email already registered");
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
    @PostMapping("/api/users/changeStatus/{id}/{status}")
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
    @PostMapping("/api/users/setActive/{id}")
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
    @PostMapping("/api/users/setPassive/{id}")
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
    @GetMapping("/api/users/customers")
    public ResponseEntity<List<CustomerUser>> getAllCustomers(){

        return ResponseEntity.ok(userService.findAllByRole(RoleType.CUSTOMER).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/merchant")
    public ResponseEntity<List<MerchantUser>> getAllMerchants(){
        return ResponseEntity.ok(userService.findAllByRole(RoleType.MERCHANT).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/merchant/passive")
    public ResponseEntity<List<MerchantUser>> getPassiveMerchantUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT,false).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/merchant/active")
    public ResponseEntity<List<MerchantUser>> getActiveMerchantUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT,true).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/customers/passive")
    public ResponseEntity<List<CustomerUser>> getPassiveCustomerUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER,false).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/customers/active")
    public ResponseEntity<List<CustomerUser>> getActiveCustomerUsers(){
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER,true).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }
}
