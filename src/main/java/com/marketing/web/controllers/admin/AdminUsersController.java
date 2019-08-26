package com.marketing.web.controllers.admin;

import com.marketing.web.models.User;
import com.marketing.web.services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/passive")
    public ResponseEntity<List<User>> getPassiveUsers(){
        return ResponseEntity.ok(userService.findAllByStatus(false));
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers(){
        return ResponseEntity.ok(userService.findAllByStatus(true));
    }

    @PostMapping("/setActive/{id}")
    public ResponseEntity<User> setActiveUser(@PathVariable Long id){
        User user = userService.findById(id);
        user.setStatus(true);
        return ResponseEntity.ok(userService.update(user,user));
    }

}
