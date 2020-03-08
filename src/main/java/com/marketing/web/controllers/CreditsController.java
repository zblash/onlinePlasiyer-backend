package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.credit.*;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Credit;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.services.credit.CreditActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.CreditMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/credits")
public class CreditsController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private CreditActivityService creditActivityService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableCredit>> getAll(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalDebt") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        return ResponseEntity.ok(CreditMapper
                .pagedCreditListToWrapperReadableCredit(creditService.findAllByCreditType(pageNumber, sortBy, sortType, CreditType.SCRD)));
    }

    @GetMapping("/my")
    public ResponseEntity<ReadableCredit> getUserCredit() {
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.findSystemCreditByUser(user)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<ReadableCredit> getCreditByUser(@PathVariable String userId) {
        User user = userService.findByUUID(userId);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.findSystemCreditByUser(user)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> updateCredit(@PathVariable String creditId, @Valid @RequestBody WritableCredit writableCredit) {
        Credit credit = CreditMapper.writableCreditToCredit(writableCredit);
        credit.setCreditType(CreditType.SCRD);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(creditService.update(creditId, credit)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{creditId}")
    public ResponseEntity<ReadableCredit> deleteCategory(@PathVariable String creditId) {
        Credit systemCredit = creditService.findByUUID(creditId);
        creditService.delete(systemCredit);
        return ResponseEntity.ok(CreditMapper.creditToReadableCredit(systemCredit));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MERCHANT')")
    @GetMapping("/users/byUser")
    public ResponseEntity<ReadableUsersCredit> getByUser(@RequestParam(required = false) String userId, @RequestParam(required = false) String userName) {
        User loggedInUser = userService.getLoggedInUser();
        User foundUser = null;
        if (userId != null && !userId.isEmpty()) {
            foundUser = userService.findByUUID(userId);
        } else if (userName != null && !userName.isEmpty()) {
            foundUser = userService.findByUserName(userName);
        }
        RoleType roleType = UserMapper.roleToRoleType(loggedInUser.getRole());
        Optional<Credit> optionalCredit = creditService.findByCustomerAndMerchant(
                RoleType.CUSTOMER.equals(roleType) ? loggedInUser : foundUser,
                RoleType.MERCHANT.equals(roleType) ? loggedInUser : foundUser
        );
        if (optionalCredit.isPresent()) {
            return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(optionalCredit.get()));
        }
        throw new BadRequestException("There is no credit");
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MERCHANT')")
    @GetMapping("/users")
    public ResponseEntity<WrapperPagination<ReadableUsersCredit>> getAllCredits(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "totalDebt") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        User loggedInUser = userService.getLoggedInUser();

        return ResponseEntity.ok(CreditMapper.pagedUsersCreditListToWrapperReadableUsersCredit(creditService.findAllByUser(loggedInUser, pageNumber, sortBy, sortType)));

    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/users")
    public ResponseEntity<ReadableUsersCredit> createCredit(@RequestBody WritableUserCredit writableUserCredit) {
        User loggedInUser = userService.getLoggedInUser();
        User customer = userService.findByUUID(writableUserCredit.getCustomerId());
        if (UserMapper.roleToRoleType(customer.getRole()).equals(RoleType.CUSTOMER)) {
            Credit credit = new Credit();
            credit.setCreditLimit(writableUserCredit.getCreditLimit());
            credit.setCustomer(customer);
            credit.setMerchant(loggedInUser);
            credit.setTotalDebt(writableUserCredit.getTotalDebt());
            credit.setCreditType(CreditType.MCRD);
            return new ResponseEntity<>(CreditMapper.usersCreditToReadableUsersCredit(creditService.create(credit)), HttpStatus.CREATED);
        }
        throw new BadRequestException("You can only create credit to CUSTOMER users");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PutMapping("/users/{id}")
    public ResponseEntity<ReadableUsersCredit> updateCredit(@PathVariable String id, @RequestBody WritableUserCredit writableUserCredit) {
        User loggedInUser = userService.getLoggedInUser();
        User customer = userService.findByUUID(writableUserCredit.getCustomerId());
        if (UserMapper.roleToRoleType(customer.getRole()).equals(RoleType.CUSTOMER)) {
            Credit credit = creditService.findByUUIDAndMerchant(id, loggedInUser);
            credit.setCreditLimit(writableUserCredit.getCreditLimit());
            credit.setCustomer(customer);
            credit.setTotalDebt(writableUserCredit.getTotalDebt());
            return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(creditService.update(credit.getUuid().toString(),credit)));
        }
        throw new BadRequestException("You can only create credit to CUSTOMER users");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MERCHANT')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ReadableUsersCredit> deleteCredit(@PathVariable String id) {
        User loggedInUser = userService.getLoggedInUser();
        Credit credit;
        if (UserMapper.roleToRoleType(loggedInUser.getRole()).equals(RoleType.ADMIN)) {
            credit = creditService.findByUUID(id);
        } else {
            credit = creditService.findByUUIDAndMerchant(id, loggedInUser);
        }
        creditService.delete(credit);
        return ResponseEntity.ok(CreditMapper.usersCreditToReadableUsersCredit(credit));
    }

    @GetMapping("/activities")
    public ResponseEntity<WrapperPagination<ReadableCreditActivity>> getCreditActivities(@RequestParam(required = false) String userId, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType) {
        User loggedInUser = userService.getLoggedInUser();

        if(!userId.isEmpty()) {
            User userById = userService.findByUUID(userId);
            return ResponseEntity.ok(CreditMapper.pagedCreditActivityListToWrapperReadableCredityActivity(creditActivityService.findAllByUsers(loggedInUser, userById,pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(CreditMapper.pagedCreditActivityListToWrapperReadableCredityActivity(creditActivityService.findAllByUser(loggedInUser, pageNumber, sortBy, sortType)));
    }
}
