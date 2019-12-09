package com.marketing.web.controllers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.obligation.ReadableObligation;
import com.marketing.web.dtos.obligation.ReadableTotalObligation;
import com.marketing.web.dtos.order.OrderSummary;
import com.marketing.web.dtos.product.ReadableProductSpecify;
import com.marketing.web.dtos.user.*;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.Address;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.*;
import com.marketing.web.utils.mappers.CityMapper;
import com.marketing.web.utils.mappers.ObligationMapper;
import com.marketing.web.utils.mappers.ProductMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CityService cityService;

    @Autowired
    private StateService stateService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private ProductSpecifyService productSpecifyService;
    private Logger logger = LoggerFactory.getLogger(UsersController.class);

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

    @PostMapping("/changeStatus/{id}/{status}")
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

    // TODO Kaldirilacak
    @PostMapping("/setActive/{id}")
    public ResponseEntity<?> setActiveUser(@PathVariable String id) {
        User user = userService.findByUUID(id);
        user.setStatus(true);
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

    // TODO Kaldirilacak
    @PostMapping("/setPassive/{id}")
    public ResponseEntity<?> setPassiveUser(@PathVariable String id) {
        User user = userService.findByUUID(id);
        user.setStatus(false);
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

    // TODO Kaldirilacak
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerUser>> getAllCustomers() {

        return ResponseEntity.ok(userService.findAllByRole(RoleType.CUSTOMER).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @GetMapping("/merchant")
    public ResponseEntity<List<MerchantUser>> getAllMerchants() {
        return ResponseEntity.ok(userService.findAllByRole(RoleType.MERCHANT).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @GetMapping("/merchant/passive")
    public ResponseEntity<List<MerchantUser>> getPassiveMerchantUsers() {
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT, false).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @GetMapping("/merchant/active")
    public ResponseEntity<List<MerchantUser>> getActiveMerchantUsers() {
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.MERCHANT, true).stream()
                .map(UserMapper::userToMerchant)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @GetMapping("/customers/passive")
    public ResponseEntity<List<CustomerUser>> getPassiveCustomerUsers() {
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER, false).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    // TODO Kaldirilacak
    @GetMapping("/customers/active")
    public ResponseEntity<List<CustomerUser>> getActiveCustomerUsers() {
        return ResponseEntity.ok(userService.findAllByRoleAndStatus(RoleType.CUSTOMER, true).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    @PostMapping("/create")
    public ResponseEntity<ReadableRegister> createUser(@Valid @RequestBody WritableRegister writableRegister) {
        User user = UserMapper.writableRegisterToUser(writableRegister);

        if (userService.canRegister(user)) {
            Address address = new Address();
            City city = cityService.findByUuid(writableRegister.getCityId());
            address.setCity(city);
            address.setState(stateService.findByUuidAndCity(writableRegister.getStateId(), city));
            address.setDetails(writableRegister.getDetails());

            user.setStatus(writableRegister.isStatus());
            user.setAddress(addressService.create(address));
            ReadableRegister readableRegister = UserMapper.userToReadableRegister(userService.create(user, writableRegister.getRoleType()));
            return ResponseEntity.ok(readableRegister);
        }
        throw new BadRequestException("Username or email already registered");
    }
    @GetMapping("/infos/{id}")
    public ResponseEntity<ReadableUserInfo> getUserInfos(@PathVariable String id){
        User user = userService.findByUUID(id);
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
    }
    @PutMapping("/updateInfos/{id}")
    public ResponseEntity<ReadableUserInfo> updateUser(@PathVariable String id, @Valid @RequestBody WritableUserInfo writableUserInfo) {
        User user = userService.findByUUID(id);
        if (writableUserInfo.getEmail().equals(user.getEmail()) || !userService.checkUserByEmail(writableUserInfo.getEmail())) {
            user.setName(writableUserInfo.getName());
            user.setEmail(writableUserInfo.getEmail());
            Address address = user.getAddress();
            State state = stateService.findByUuid(writableUserInfo.getAddress().getStateId());
            City city = cityService.findByUuid(writableUserInfo.getAddress().getCityId());
            address.setState(state);
            address.setCity(city);
            address.setDetails(writableUserInfo.getAddress().getDetails());
            user.setAddress(addressService.update(address.getId(), address));
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId(), user)));
        }
        throw new BadRequestException("Email already registered");
    }
    @PostMapping("/addActiveState/{id}")
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


}