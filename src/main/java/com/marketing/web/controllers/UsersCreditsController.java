package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.ReadableUsersCredit;
import com.marketing.web.dtos.credit.WritableUserCredit;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.User;
import com.marketing.web.models.UsersCredit;
import com.marketing.web.services.credit.UsersCreditService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.CreditMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/credits")
public class UsersCreditsController {

    @Autowired
    private UsersCreditService usersCreditService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<ReadableUsersCredit>> getAllCredits(@RequestParam(required = false) String userId){
        User loggedInUser = userService.getLoggedInUser();
        User user = UserMapper.roleToRoleType(loggedInUser.getRole()).equals(RoleType.ADMIN) ? userService.findByUUID(userId) : loggedInUser;
        return ResponseEntity.ok(usersCreditService.findAllByUser(user).stream().map(CreditMapper::usersCreditToReadableUsersCredit).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @PostMapping
    public ResponseEntity<ReadableUsersCredit> createCredit(@RequestBody WritableUserCredit writableUserCredit){
        User loggedInUser = userService.getLoggedInUser();
        User customer = userService.findByUUID(writableUserCredit.getCustomerId());
        User merchant = UserMapper.roleToRoleType(loggedInUser.getRole()).equals(RoleType.MERCHANT) ? loggedInUser : userService.findByUUID(writableUserCredit.getMerchantId());
        if (UserMapper.roleToRoleType(customer.getRole()).equals(RoleType.CUSTOMER)) {
            UsersCredit usersCredit = new UsersCredit();
            usersCredit.setCreditLimit(writableUserCredit.getCreditLimit());
            usersCredit.setCustomer(customer);
            usersCredit.setMerchant(merchant);
            usersCredit.setTotalDebt(writableUserCredit.getTotalDebt());
            return new ResponseEntity<>(CreditMapper.usersCreditToReadableUsersCredit(usersCreditService.create(usersCredit)), HttpStatus.CREATED);
        }
        throw new BadRequestException("You can only create credit to CUSTOMER users");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableUsersCredit> updateCredit(@PathVariable String id, @RequestBody WritableUserCredit writableUserCredit){
        User loggedInUser = userService.getLoggedInUser();
        User customer = userService.findByUUID(writableUserCredit.getCustomerId());
        User merchant = UserMapper.roleToRoleType(loggedInUser.getRole()).equals(RoleType.MERCHANT) ? loggedInUser : userService.findByUUID(writableUserCredit.getMerchantId());
        if (UserMapper.roleToRoleType(customer.getRole()).equals(RoleType.CUSTOMER)) {
            UsersCredit usersCredit = usersCreditService.findByUUIDAndMerchant(id, merchant);
            usersCredit.setCreditLimit(writableUserCredit.getCreditLimit());
            usersCredit.setCustomer(customer);
            usersCredit.setTotalDebt(writableUserCredit.getTotalDebt());
            return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(usersCreditService.create(usersCredit)));
        }
        throw new BadRequestException("You can only create credit to CUSTOMER users");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableUsersCredit> deleteCredit(@PathVariable String id){
        User loggedInUser = userService.getLoggedInUser();
        UsersCredit usersCredit;
        if (UserMapper.roleToRoleType(loggedInUser.getRole()).equals(RoleType.ADMIN)){
            usersCredit = usersCreditService.findByUUID(id);
        } else {
            usersCredit = usersCreditService.findByUUIDAndMerchant(id, loggedInUser);
        }
        usersCreditService.delete(usersCredit);
        return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(usersCredit));
    }
}