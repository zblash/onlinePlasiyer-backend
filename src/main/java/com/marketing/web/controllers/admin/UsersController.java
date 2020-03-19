package com.marketing.web.controllers.admin;

import com.marketing.web.dtos.product.WritableCommission;
import com.marketing.web.dtos.user.*;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.HttpMessage;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartServiceImpl;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.*;
import com.marketing.web.utils.mappers.CityMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private CityService cityService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    private Logger logger = LoggerFactory.getLogger(UsersController.class);

    @GetMapping
    public ResponseEntity<List<ReadableUserInfo>> getAllUser() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserMapper::userToReadableUserInfo).collect(Collectors.toList()));
    }

    @GetMapping("/{roleType}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String roleType, @RequestParam(required = false) Boolean status) {
        RoleType role = RoleType.valueOf(roleType.toUpperCase());
        List<User> users;

        if (status != null) {
            users = userService.findAllByRoleAndStatus(role, status);
        } else {
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

    @PostMapping("/status/{id}/{status}")
    public ResponseEntity<?> changeUserStatus(@PathVariable String id, @PathVariable boolean status) {
        User user = userService.findByUUID(id);
        user.setStatus(status);
        user = userService.update(user.getId(), user);
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

    @PostMapping
    public ResponseEntity<ReadableRegister> createUser(@Valid @RequestBody WritableRegister writableRegister) {
        User user = UserMapper.writableRegisterToUser(writableRegister);

        if (userService.canRegister(user)) {
            user.setStatus(writableRegister.isStatus());
            City city = cityService.findByUuid(writableRegister.getCityId());
            user.setCity(city);
            user.setState(stateService.findByUuidAndCity(writableRegister.getStateId(), city));
            User createdUser = userService.create(user, writableRegister.getRoleType());
            RoleType roleType = UserMapper.roleToRoleType(createdUser.getRole());
            if (roleType.equals(RoleType.CUSTOMER)) {
                Cart cart = cartService.create(createdUser);
                user.setCart(cart);
                Credit credit = new Credit();
                credit.setCustomer(createdUser);
                credit.setTotalDebt(0);
                credit.setCreditLimit(0);
                credit.setCreditType(CreditType.SYSTEM_CREDIT);
                creditService.create(credit);
            } else if (roleType.equals(RoleType.MERCHANT)) {
                Obligation obligation = new Obligation();
                obligation.setUser(createdUser);
                obligation.setReceivable(0);
                obligation.setReceivable(0);
                obligationService.create(obligation);
            }
            return new ResponseEntity<>(UserMapper.userToReadableRegister(createdUser), HttpStatus.CREATED);
        }
        throw new BadRequestException("Username or email already registered");
    }
    @GetMapping("/info/{id}")
    public ResponseEntity<ReadableUserInfo> getUserInfos(@PathVariable String id){
        User user = userService.findByUUID(id);
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
    }
    @PutMapping("/info/{id}")
    public ResponseEntity<ReadableUserInfo> updateUser(@PathVariable String id, @Valid @RequestBody WritableUserInfo writableUserInfo) {
        User user = userService.findByUUID(id);
        if (writableUserInfo.getEmail().equals(user.getEmail()) || !userService.checkUserByEmail(writableUserInfo.getEmail())) {
            user.setName(writableUserInfo.getName());
            user.setEmail(writableUserInfo.getEmail());
            State state = stateService.findByUuid(writableUserInfo.getAddress().getStateId());
            City city = cityService.findByUuid(writableUserInfo.getAddress().getCityId());
            user.setState(state);
            user.setCity(city);
            user.setAddressDetails(writableUserInfo.getAddress().getDetails());
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId(), user)));
        }
        throw new BadRequestException("Email already registered");
    }
    @PostMapping("/activeStates/{id}")
    public ResponseEntity<List<ReadableState>> addActiveState(@PathVariable String id, @RequestBody List<String> states){
        User user = userService.findByUUID(id);
        List<State> stateList = stateService.findAllByUuids(states);
        List<State> addedList = user.getActiveStates();
        addedList.addAll(stateList);
        user.setActiveStates(addedList.stream().distinct().collect(Collectors.toList()));
        userService.update(user.getId(),user);
        return ResponseEntity.ok(addedList.stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/activeStates/{id}")
    public ResponseEntity<List<ReadableState>> getActiveStates(@PathVariable String id){
        User user = userService.findByUUID(id);
        return ResponseEntity.ok(user.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @PutMapping("/commissions")
    public ResponseEntity<ReadableUserInfo> setCommissions(@Valid @RequestBody WritableCommission writableCommission) {
        User user = userService.findByUUID(writableCommission.getId());
        RoleType roleType = UserMapper.roleToRoleType(user.getRole());
        if (roleType.equals(RoleType.MERCHANT)) {
            user.setCommission(writableCommission.getCommission());
            List<ProductSpecify> productSpecifies = productSpecifyService.findAllByUserWithoutPagination(user)
                    .stream()
                    .peek(productSpecify -> productSpecify.setCommission(writableCommission.getCommission()))
                    .collect(Collectors.toList());
            productSpecifyService.updateAll(productSpecifies);
            userService.update(user.getId(), user);
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
        }
        throw new BadRequestException("Only merchant user's commission editable");
    }

    @PostMapping("/changePassword/{id}")
    public ResponseEntity<HttpMessage> changeUserPassword(@PathVariable String id, @Valid @RequestBody WritablePasswordChange writablePasswordReset, WebRequest request){
        User user = userService.findByUUID(id);

        if(writablePasswordReset.getPassword().equals(writablePasswordReset.getPasswordConfirmation())){
            userService.changePassword(user, writablePasswordReset.getPassword());
            HttpMessage httpMessage = new HttpMessage(HttpStatus.OK);
            httpMessage.setMessage("Password changed");
            httpMessage.setPath(((ServletWebRequest)request).getRequest().getRequestURL().toString());
            return ResponseEntity.ok(httpMessage);
        }
        throw new BadRequestException("Fields not matching");
    }
}
