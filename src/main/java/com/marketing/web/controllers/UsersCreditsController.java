package com.marketing.web.controllers;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/credits")
public class UsersCreditsController {

    @Autowired
    private UsersCreditService usersCreditService;

    @Autowired
    private UserService userService;

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
            return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(usersCreditService.create(usersCredit)));
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
            UsersCredit usersCredit = usersCreditService.findByUUID(id);
            usersCredit.setCreditLimit(writableUserCredit.getCreditLimit());
            usersCredit.setCustomer(customer);
            usersCredit.setMerchant(merchant);
            usersCredit.setTotalDebt(writableUserCredit.getTotalDebt());
            return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(usersCreditService.create(usersCredit)));
        }
        throw new BadRequestException("You can only create credit to CUSTOMER users");
    }


}
