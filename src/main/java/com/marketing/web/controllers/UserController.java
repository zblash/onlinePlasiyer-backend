package com.marketing.web.controllers;

import com.marketing.web.dtos.user.CustomerUser;
import com.marketing.web.dtos.user.LoginDTO;
import com.marketing.web.dtos.user.MerchantUser;
import com.marketing.web.dtos.user.RegisterDTO;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Address;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.user.AddressService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody(required = true) Map<String,String> login){
        User userDetails = userService.findByUserName(login.get("username"));

        if (userDetails.isStatus() && passwordEncoder.matches(login.get("password"),userDetails.getPassword())){
            String jwt= JWTGenerator.generate(userDetails);
            LoginDTO.LoginDTOBuilder loginDTOBuilder = new LoginDTO.LoginDTOBuilder(jwt);
            loginDTOBuilder.email(userDetails.getEmail());
            loginDTOBuilder.name(userDetails.getName());
            loginDTOBuilder.userName(userDetails.getUserName());
            String role = userDetails.getRole().getName().split("_")[1];
            loginDTOBuilder.role(role);
            loginDTOBuilder.address(userDetails.getAddress());
            loginDTOBuilder.activeStates(userDetails.getActiveStates().stream().map(State::getTitle).collect(Collectors.toList()));
            LoginDTO loginDTO = loginDTOBuilder
                    .build();
            return new ResponseEntity<>(loginDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody RegisterDTO registerDTO){
        User user = UserMapper.INSTANCE.registerDTOToUser(registerDTO);
        Address address = addressService.create(UserMapper.INSTANCE.registerDTOToAddress(registerDTO));
        user.setStatus(true);
        user.setAddress(address);
        userService.create(user,registerDTO.getRoleType());
        return new ResponseEntity<>(user, new HttpHeaders(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/api/users/addActiveState")
    public ResponseEntity<?> addActiveState(@RequestBody List<String> states){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        List<State> stateList = stateRepository.findAllByTitleIn(states);
        List<State> addedList = user.getActiveStates();
        addedList.addAll(stateList);
        user.setActiveStates(addedList.stream().distinct().collect(Collectors.toList()));
        userService.update(user.getId(),user);
        return ResponseEntity.ok("Added active states");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/api/users/activeStates")
    public ResponseEntity<?> getActiveStates(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        return ResponseEntity.ok(user.getActiveStates().stream().map(State::getTitle).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/customers")
    public ResponseEntity<List<CustomerUser>> getAllCustomers(){

        return ResponseEntity.ok(userService.findAllByRole(RoleType.CUSTOMER).stream()
                .map(UserMapper.INSTANCE::userToCustomer)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/merchant")
    public ResponseEntity<List<MerchantUser>> getAllMerchants(){
        return ResponseEntity.ok(userService.findAllByRole(RoleType.MERCHANT).stream()
                .map(UserMapper.INSTANCE::userToMerchant)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/passive")
    public ResponseEntity<List<User>> getPassiveUsers(){
        return ResponseEntity.ok(userService.findAllByStatus(false));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/api/users/active")
    public ResponseEntity<List<User>> getActiveUsers(){
        return ResponseEntity.ok(userService.findAllByStatus(true));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/api/users/setActive/{id}")
    public ResponseEntity<User> setActiveUser(@PathVariable Long id){
        User user = userService.findById(id);
        user.setStatus(true);
        return ResponseEntity.ok(userService.update(user.getId(),user));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/api/users/setPassive/{id}")
    public ResponseEntity<User> setPassiveUser(@PathVariable Long id){
        User user = userService.findById(id);
        user.setStatus(false);
        return ResponseEntity.ok(userService.update(user.getId(),user));
    }
}
