package com.marketing.web.controllers;

import com.marketing.web.dtos.LoginDTO;
import com.marketing.web.dtos.RegisterDTO;
import com.marketing.web.models.Address;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.impl.AddressService;
import com.marketing.web.services.impl.UserService;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody(required = true) Map<String,String> login){
        User userDetails = userService.findByUserName(login.get("userName"));

        if (userDetails.isStatus() && passwordEncoder.matches(login.get("password"),userDetails.getPassword())){
            String jwt= JWTGenerator.generate(userDetails);
            LoginDTO.LoginDTOBuilder loginDTOBuilder = new LoginDTO.LoginDTOBuilder(jwt);
            loginDTOBuilder.email(userDetails.getEmail());
            loginDTOBuilder.name(userDetails.getName());
            loginDTOBuilder.userName(userDetails.getUserName());
            loginDTOBuilder.role(userDetails.getRole().getName());
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

}
