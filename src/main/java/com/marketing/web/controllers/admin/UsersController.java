package com.marketing.web.controllers.admin;

import com.marketing.web.dtos.product.WritableCommission;
import com.marketing.web.dtos.user.readable.ReadableRegister;
import com.marketing.web.dtos.user.readable.ReadableState;
import com.marketing.web.dtos.user.readable.ReadableUserInfo;
import com.marketing.web.dtos.user.register.WritableRegister;
import com.marketing.web.dtos.user.writable.WritablePasswordChange;
import com.marketing.web.dtos.user.writable.WritableUserInfo;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/private/admin/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UsersController {

    private final UserService userService;

    private final CityService cityService;

    private final StateService stateService;

    private final CartServiceImpl cartService;

    private final CreditService creditService;

    private final ObligationService obligationService;

    private final ProductSpecifyService productSpecifyService;

    private final MerchantService merchantService;

    private final CustomerService customerService;

    private Logger logger = LoggerFactory.getLogger(UsersController.class);

    public UsersController(ObligationService obligationService, UserService userService, CityService cityService, StateService stateService, CartServiceImpl cartService, CreditService creditService, ProductSpecifyService productSpecifyService, MerchantService merchantService, CustomerService customerService) {
        this.obligationService = obligationService;
        this.userService = userService;
        this.cityService = cityService;
        this.stateService = stateService;
        this.cartService = cartService;
        this.creditService = creditService;
        this.productSpecifyService = productSpecifyService;
        this.merchantService = merchantService;
        this.customerService = customerService;
    }

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
                return ResponseEntity.ok(customerService.findAllByUsers(users).stream()
                        .map(UserMapper::userToCustomer)
                        .collect(Collectors.toList()));
            case MERCHANT:
                return ResponseEntity.ok(merchantService.findAllByUsers(users).stream()
                        .map(UserMapper::userToMerchant)
                        .collect(Collectors.toList()));
            default:
                return ResponseEntity.ok(users.stream()
                        .map(UserMapper::userToAdmin)
                        .collect(Collectors.toList()));
        }
    }

    @PostMapping("/status/{roleType}/{id}/{status}")
    public ResponseEntity<?> changeUserStatus(@PathVariable RoleType roleType, @PathVariable String id, @PathVariable boolean status) {
        User user;
        switch (roleType) {
            case CUSTOMER:
                Customer customer = customerService.findById(id);
                user = customer.getUser();
                user.setStatus(status);
                userService.update(user.getId().toString(), user);
                customer.setUser(user);
                return ResponseEntity.ok(UserMapper.userToCustomer(customer));
            case MERCHANT:
                Merchant merchant = merchantService.findById(id);
                user = merchant.getUser();
                user.setStatus(status);
                userService.update(user.getId().toString(), user);
                merchant.setUser(user);
                return ResponseEntity.ok(UserMapper.userToMerchant(merchant));
            default:
                user = userService.findById(id);
                user.setStatus(status);
                userService.update(user.getId().toString(), user);
                return ResponseEntity.ok(UserMapper.userToAdmin(user));
        }
    }

    @PostMapping
    public ResponseEntity<ReadableRegister> createUser(@Valid @RequestBody WritableRegister writableRegister) {
        User user = UserMapper.writableRegisterToUser(writableRegister);

        if (userService.canRegister(user)) {
            user.setStatus(writableRegister.isStatus());
            City city = cityService.findById(writableRegister.getCityId());
            user.setCity(city);
            user.setPhoneNumber(writableRegister.getPhoneNumber());
            user.setState(stateService.findByUuidAndCity(writableRegister.getStateId(), city));
            User createdUser = userService.create(user, writableRegister.getRoleType());
            RoleType roleType = UserMapper.roleToRoleType(createdUser.getRole());
            if (roleType.equals(RoleType.CUSTOMER)) {
                Customer customer = new Customer();
                customer.setUser(createdUser);
                customer.setTaxNumber(writableRegister.getTaxNumber());
                customerService.create(customer);

                cartService.create(customer);

                Credit credit = new Credit();
                credit.setCustomer(customer);
                credit.setCreditType(CreditType.SYSTEM_CREDIT);
                credit.setTotalDebt(BigDecimal.ZERO);
                credit.setCreditLimit(BigDecimal.ZERO);

                creditService.create(credit);
            } else if (roleType.equals(RoleType.MERCHANT)) {
                Merchant merchant = new Merchant();
                merchant.setUser(createdUser);
                merchant.setTaxNumber(writableRegister.getTaxNumber());

                if (writableRegister.getActiveStates() != null) {
                    Set<State> stateList = new HashSet<>(stateService.findAllByIds(new ArrayList<>(writableRegister.getActiveStates())));
                    merchant.setActiveStates(stateList);
                }
                merchantService.create(merchant);

                Obligation obligation = new Obligation();
                obligation.setMerchant(merchant);
                obligationService.create(obligation);
            }
            return new ResponseEntity<>(UserMapper.userToReadableRegister(createdUser), HttpStatus.CREATED);
        }
        throw new BadRequestException("Username or email already registered");
    }
    @GetMapping("/{roleType}/info/{id}")
    public ResponseEntity<ReadableUserInfo> getUserInfos(@PathVariable RoleType roleType, @PathVariable String id){
        switch (roleType) {
            case MERCHANT:
                Merchant merchant = merchantService.findById(id);
                return ResponseEntity.ok(UserMapper.userToReadableUserInfo(merchant));
            case CUSTOMER:
                Customer customer = customerService.findById(id);
                return ResponseEntity.ok(UserMapper.userToReadableUserInfo(customer));
            default:
                User user = userService.findById(id);
                return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
        }
    }
    @PutMapping("/{roleType}/info/{id}")
    public ResponseEntity<ReadableUserInfo> updateUser(@PathVariable RoleType roleType, @PathVariable String id, @Valid @RequestBody WritableUserInfo writableUserInfo) {
        User user;
        switch (roleType) {
            case MERCHANT:
                user = merchantService.findById(id).getUser();
                break;
            case CUSTOMER:
                user = customerService.findById(id).getUser();
                break;
            default:
                user = userService.findById(id);
                break;
        }
        if (writableUserInfo.getEmail().equals(user.getEmail()) || !userService.checkUserByEmail(writableUserInfo.getEmail())) {
            user.setName(writableUserInfo.getName());
            user.setEmail(writableUserInfo.getEmail());
            State state = stateService.findById(writableUserInfo.getAddress().getStateId());
            City city = cityService.findById(writableUserInfo.getAddress().getCityId());
            user.setState(state);
            user.setCity(city);
            user.setAddressDetails(writableUserInfo.getAddress().getDetails());
            RoleType role = UserMapper.roleToRoleType(user.getRole());
            if(role.equals(RoleType.MERCHANT)) {
                return ResponseEntity.ok(UserMapper.userToReadableUserInfo(merchantService.findByUser(userService.update(user.getId().toString(), user))));
            } else if(role.equals(RoleType.CUSTOMER)) {
                return ResponseEntity.ok(UserMapper.userToReadableUserInfo(customerService.findByUser(userService.update(user.getId().toString(), user))));
            }
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId().toString(), user)));
        }
        throw new BadRequestException("Email already registered");
    }
    @PostMapping("/activeStates/{id}")
    public ResponseEntity<List<ReadableState>> addActiveState(@PathVariable String id, @RequestBody List<String> states){
        Merchant merchant = merchantService.findById(id);
        List<State> stateList = stateService.findAllByIds(states);
        Set<State> merchantStates = merchant.getActiveStates();
        merchantStates.addAll(stateList);
        merchant.setActiveStates(merchantStates);
        merchantService.update(merchant.getId().toString(), merchant);
        return ResponseEntity.ok(merchantStates.stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/activeStates/{id}")
    public ResponseEntity<List<ReadableState>> getActiveStates(@PathVariable String id){
        Merchant merchant = merchantService.findById(id);
        return ResponseEntity.ok(merchant.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @PutMapping("/commissions")
    public ResponseEntity<ReadableUserInfo> setCommissions(@Valid @RequestBody WritableCommission writableCommission) {
        Merchant merchant = merchantService.findById(writableCommission.getId());
            merchant.setCommission(writableCommission.getCommission());
            List<ProductSpecify> productSpecifies = productSpecifyService.findAllByMerchant(merchant)
                    .stream()
                    .peek(productSpecify -> productSpecify.setCommission(writableCommission.getCommission()))
                    .collect(Collectors.toList());
            productSpecifyService.updateAll(productSpecifies);
            merchantService.update(merchant.getId().toString(), merchant);
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(merchant));
    }

    @PostMapping("/{roleType}/changePassword/{id}")
    public ResponseEntity<HttpMessage> changeUserPassword(@PathVariable RoleType roleType, @PathVariable String id, @Valid @RequestBody WritablePasswordChange writablePasswordReset, WebRequest request){
        User user;
        switch (roleType) {
            case MERCHANT:
                user = merchantService.findById(id).getUser();
                break;
            case CUSTOMER:
                user = customerService.findById(id).getUser();
                break;
            default:
                user = userService.findById(id);
                break;
        }

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
