package com.marketing.web.controllers;

import com.marketing.web.dtos.user.*;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.HttpMessage;
import com.marketing.web.models.Address;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.user.AddressService;
import com.marketing.web.services.user.CityService;
import com.marketing.web.services.user.StateService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.CityMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthController {


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AddressService addressService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;
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


    @PostMapping("/api/user/changePassword")
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
    @PostMapping("/api/user/addActiveState")
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
    @GetMapping("/api/user/activeStates")
    public ResponseEntity<List<ReadableState>> getActiveStates(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(user.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/api/user/getinfos")
    public ResponseEntity<ReadableUserInfo> getUserInfos(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
    }

    @PostMapping("/api/user/updateInfos")
    public ResponseEntity<ReadableUserInfo> updateUserInfo(@Valid @RequestBody WritableUserInfo writableUserInfo){
        User user = userService.getLoggedInUser();
        if (writableUserInfo.getEmail().equals(user.getEmail()) && !userService.checkUserByEmail(writableUserInfo.getEmail())){
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
}