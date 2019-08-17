package com.marketing.web.controllers;

import com.marketing.web.dtos.LoginDTO;
import com.marketing.web.models.User;
import com.marketing.web.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody(required = true) Map<String,String> login){
        User userDetails = userService.findByUserName(login.get("userName"));

        if (passwordEncoder.matches(login.get("password"),userDetails.getPassword())){
            String jwt= JWTGenerator.generate(userDetails);
            LoginDTO loginDTO = new LoginDTO.LoginDTOBuilder(jwt)
                    .email(userDetails.getEmail())
                    .name(userDetails.getName())
                    .userName(userDetails.getUserName())
                    .build();
            return new ResponseEntity<>(loginDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody User user){
        userService.create(user);
        return new ResponseEntity<>(user, new HttpHeaders(), HttpStatus.OK);
    }
}
