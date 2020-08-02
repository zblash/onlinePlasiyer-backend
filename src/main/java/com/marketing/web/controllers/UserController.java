package com.marketing.web.controllers;

import com.marketing.web.configs.constants.ApplicationContstants;
import com.marketing.web.dtos.announcement.ReadableAnnouncement;
import com.marketing.web.dtos.user.readable.*;
import com.marketing.web.dtos.user.register.WritableCustomerRegister;
import com.marketing.web.dtos.user.register.WritableMerchantRegister;
import com.marketing.web.dtos.user.writable.*;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.RoleType;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.HttpMessage;
import com.marketing.web.models.*;
import com.marketing.web.configs.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.services.announcement.AnnouncementService;
import com.marketing.web.services.cart.CartServiceImpl;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.user.*;
import com.marketing.web.utils.MailUtil;
import com.marketing.web.utils.RandomStringGenerator;
import com.marketing.web.utils.mappers.AnnouncementMapper;
import com.marketing.web.utils.mappers.CityMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserController {


    private final UserService userService;

    private final StateService stateService;

    private final CityService cityService;

    private final CartServiceImpl cartService;

    private final CreditService creditService;

    private final ObligationService obligationService;

    private final MailUtil mailUtil;

    private final AnnouncementService announcementService;

    private final MerchantService merchantService;

    private final CustomerService customerService;

    public UserController(UserService userService, StateService stateService, CityService cityService, CartServiceImpl cartService, CreditService creditService, ObligationService obligationService, MailUtil mailUtil, AnnouncementService announcementService, MerchantService merchantService, CustomerService customerService) {
        this.userService = userService;
        this.stateService = stateService;
        this.cityService = cityService;
        this.cartService = cartService;
        this.creditService = creditService;
        this.obligationService = obligationService;
        this.mailUtil = mailUtil;
        this.announcementService = announcementService;
        this.merchantService = merchantService;
        this.customerService = customerService;
    }
    //TODO Move from here
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody WritableLogin writableLogin, WebRequest request) {
        User user = userService.findByUserName(writableLogin.getUsername());

        if (userService.loginControl(writableLogin.getUsername(), writableLogin.getPassword())) {
            Map<String, Object> body = new HashMap<>();
            body.put("role", user.getRole().getName());
            body.put("userId", user.getId().toString());
            ReadableLogin.LoginDTOBuilder loginDTOBuilder = new ReadableLogin.LoginDTOBuilder(JWTGenerator.generate(ApplicationContstants.JWT_SECRET, null, 86_400_000, body));
            loginDTOBuilder.email(user.getEmail());
            loginDTOBuilder.name(user.getName());
            loginDTOBuilder.userName(user.getUsername());
            String role = user.getRole().getName().split("_")[1];
            loginDTOBuilder.role(role);
            return ResponseEntity.ok(loginDTOBuilder.build());
        }
        HttpMessage httpMessage = new HttpMessage(HttpStatus.UNAUTHORIZED);
        httpMessage.setMessage("Given username or password incorrect");
        httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, httpMessage.getStatus());
    }

    @PostMapping("/merchant/login")
    public ResponseEntity<?> merchantLogin(@RequestBody WritableLogin writableLogin, WebRequest request) {
        User user = userService.findByUserName(writableLogin.getUsername());

        if (userService.loginControl(writableLogin.getUsername(), writableLogin.getPassword())) {
            Merchant merchant = merchantService.findByUser(user);
            Map<String, Object> body = new HashMap<>();
            body.put("role", user.getRole().getName());
            body.put("userId", user.getId().toString());
            body.put("merchantId", merchant.getId().toString());

            ReadableAddress address = new ReadableAddress();
            address.setCityId(user.getCity().getId().toString());
            address.setCityName(user.getCity().getTitle());
            address.setStateId(user.getState().getId().toString());
            address.setStateName(user.getState().getTitle());
            address.setDetails(user.getAddressDetails());

            ReadableLogin.LoginDTOBuilder loginDTOBuilder = new ReadableLogin.LoginDTOBuilder(JWTGenerator.generate(ApplicationContstants.JWT_SECRET, null, 86_400_000, body));
            loginDTOBuilder.email(user.getEmail());
            loginDTOBuilder.name(user.getName());
            loginDTOBuilder.userName(user.getUsername());
            String role = user.getRole().getName().split("_")[1];
            loginDTOBuilder.role(role);
            loginDTOBuilder.address(address);
            loginDTOBuilder.activeStates(merchant.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));

            return ResponseEntity.ok(loginDTOBuilder.build());
        }
        HttpMessage httpMessage = new HttpMessage(HttpStatus.UNAUTHORIZED);
        httpMessage.setMessage("Given username or password incorrect");
        httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, httpMessage.getStatus());
    }

    @PostMapping("/customer/login")
    public ResponseEntity<?> customerLogin(@RequestBody WritableLogin writableLogin, WebRequest request) {
        User user = userService.findByUserName(writableLogin.getUsername());

        if (userService.loginControl(writableLogin.getUsername(), writableLogin.getPassword())) {
            Customer customer = customerService.findByUser(user);
            Map<String, Object> body = new HashMap<>();
            body.put("role", user.getRole().getName());
            body.put("userId", user.getId().toString());
            body.put("customerId", customer.getId().toString());

            ReadableAddress address = new ReadableAddress();
            address.setCityId(user.getCity().getId().toString());
            address.setCityName(user.getCity().getTitle());
            address.setStateId(user.getState().getId().toString());
            address.setStateName(user.getState().getTitle());
            address.setDetails(user.getAddressDetails());

            ReadableLogin.LoginDTOBuilder loginDTOBuilder = new ReadableLogin.LoginDTOBuilder(JWTGenerator.generate(ApplicationContstants.JWT_SECRET, null, 86_400_000, body));
            loginDTOBuilder.email(user.getEmail());
            loginDTOBuilder.name(user.getName());
            loginDTOBuilder.userName(user.getUsername());
            String role = user.getRole().getName().split("_")[1];
            loginDTOBuilder.role(role);
            loginDTOBuilder.address(address);
            ReadableLogin readableLogin = loginDTOBuilder
                    .build();
            return ResponseEntity.ok(readableLogin);
        }

        HttpMessage httpMessage = new HttpMessage(HttpStatus.UNAUTHORIZED);
        httpMessage.setMessage("Given username or password incorrect");
        httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, httpMessage.getStatus());

    }

    @PostMapping("/customer/register")
    public ResponseEntity<ReadableRegister> customerRegister(@Valid @RequestBody WritableCustomerRegister writableRegister) {
        User user = UserMapper.writableRegisterToUser(writableRegister);
        if (userService.canRegister(user)) {

            City city = cityService.findById(writableRegister.getCityId());

            user.setStatus(true);
            user.setActivationToken(RandomStringGenerator.generateId());
            user.setCity(city);
            user.setPhoneNumber(writableRegister.getPhoneNumber());
            user.setState(stateService.findByUuidAndCity(writableRegister.getStateId(), city));

            User createdUser = userService.create(user, RoleType.CUSTOMER);

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

            return ResponseEntity.ok(UserMapper.userToReadableRegister(createdUser));
        }
        throw new BadRequestException("Username or email already registered");
    }

    @PostMapping("/merchant/register")
    public ResponseEntity<ReadableRegister> merchantRegister(@Valid @RequestBody WritableMerchantRegister writableRegister) {
        User user = UserMapper.writableRegisterToUser(writableRegister);
        if (userService.canRegister(user)) {

            City city = cityService.findById(writableRegister.getCityId());

            user.setStatus(true);
            user.setActivationToken(RandomStringGenerator.generateId());
            user.setCity(city);
            user.setPhoneNumber(writableRegister.getPhoneNumber());
            user.setState(stateService.findByUuidAndCity(writableRegister.getStateId(), city));

            User createdUser = userService.create(user, RoleType.MERCHANT);

            Merchant merchant = new Merchant();
            merchant.setUser(createdUser);
            merchant.setTaxNumber(writableRegister.getTaxNumber());

            Set<State> stateList = new HashSet<>(stateService.findAllByIds(new ArrayList<>(writableRegister.getActiveStates())));
            merchant.setActiveStates(stateList);
            merchantService.create(merchant);

            Obligation obligation = new Obligation();
            obligation.setMerchant(merchant);
            obligation.setDebt(BigDecimal.ZERO);
            obligation.setReceivable(BigDecimal.ZERO);
            obligationService.create(obligation);

            return ResponseEntity.ok(UserMapper.userToReadableRegister(createdUser));
        }
        throw new BadRequestException("Username or email already registered");
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<HttpMessage> forgottenPassword(@RequestBody WritableForgotPassword writableForgotPassword, WebRequest request, Locale locale) {
        User user;
        if (!writableForgotPassword.getEmail().isEmpty()) {
            user = userService.findByEmail(writableForgotPassword.getEmail());
        } else if (!writableForgotPassword.getUsername().isEmpty()) {
            user = userService.findByUserName(writableForgotPassword.getUsername());
        } else {
            throw new BadRequestException("One of username or email is required");
        }
        user.setPasswordResetToken(RandomStringGenerator.generateId());
        user.setResetTokenExpireTime(
                Date.from(LocalDateTime.now().plus(1, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant()));
        mailUtil.sendPasswordResetMail(user.getEmail(), user.getPasswordResetToken(), locale);
        userService.update(user.getId().toString(), user);
        HttpMessage httpMessage = new HttpMessage(HttpStatus.OK);
        httpMessage.setMessage("Password reset token has been sent to to your email");
        httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
        return ResponseEntity.ok(httpMessage);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<HttpMessage> passwordReset(@Valid @RequestBody WritablePasswordReset writablePasswordReset, WebRequest request) {
        User user = userService.findByResetToken(writablePasswordReset.getToken());
        Date now = new Date();
        if (now.compareTo(user.getResetTokenExpireTime()) < 0 &&
                writablePasswordReset.getPassword().equals(writablePasswordReset.getPasswordConfirmation())) {
            userService.changePassword(user, writablePasswordReset.getPassword());
            HttpMessage httpMessage = new HttpMessage(HttpStatus.OK);
            httpMessage.setMessage("Password changed");
            httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
            return ResponseEntity.ok(httpMessage);
        }
        throw new BadRequestException("Fields not matching");
    }

    @PutMapping("/activation")
    public ResponseEntity<ReadableUserInfo> userActivation(@Valid @RequestBody WritableActivation writableActivation) {
        User user = userService.findByActivationToken(writableActivation.getActivationToken());
        user.setStatus(true);
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if(role.equals(RoleType.MERCHANT)) {
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(merchantService.findByUser(userService.update(user.getId().toString(), user))));
        }
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId().toString(), user)));
    }

    @PostMapping("/private/user/changePassword")
    public ResponseEntity<HttpMessage> changeUserPassword(@Valid @RequestBody WritablePasswordChange writablePasswordReset, WebRequest request) {
        User user = userService.getLoggedInUser();

        if (writablePasswordReset.getPassword().equals(writablePasswordReset.getPasswordConfirmation())) {
            userService.changePassword(user, writablePasswordReset.getPassword());
            HttpMessage httpMessage = new HttpMessage(HttpStatus.OK);
            httpMessage.setMessage("Password changed");
            httpMessage.setPath(((ServletWebRequest) request).getRequest().getRequestURL().toString());
            return ResponseEntity.ok(httpMessage);
        }
        throw new BadRequestException("Fields not matching");
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @PostMapping("/private/user/activeStates")
    public ResponseEntity<List<ReadableState>> addActiveState(@RequestBody List<String> states) {
        Merchant merchant = merchantService.getLoggedInMerchant();
        List<State> stateList = stateService.findAllByIds(states);
        Set<State> merchantStates = merchant.getActiveStates();
        merchantStates.addAll(stateList);
        merchant.setActiveStates(merchantStates);
        merchantService.update(merchant.getId().toString(), merchant);
        return ResponseEntity.ok(merchantStates.stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @GetMapping("/private/user/activeStates")
    public ResponseEntity<List<ReadableState>> getActiveStates() {
        User user = userService.getLoggedInUser();
        Merchant merchant = merchantService.findByUser(user);
        return ResponseEntity.ok(merchant.getActiveStates().stream().map(CityMapper::stateToReadableState).collect(Collectors.toList()));
    }

    @GetMapping("/private/user/info")
    public ResponseEntity<ReadableUserInfo> getUserInfos() {
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if(role.equals(RoleType.MERCHANT)) {
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(merchantService.findByUser(user)));
        }
        return ResponseEntity.ok(UserMapper.userToReadableUserInfo(user));
    }

    @PutMapping("/private/user/info")
    public ResponseEntity<ReadableUserInfo> updateUserInfo(@Valid @RequestBody WritableUserInfo writableUserInfo) {
        User user = userService.getLoggedInUser();
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
            }
            return ResponseEntity.ok(UserMapper.userToReadableUserInfo(userService.update(user.getId().toString(), user)));
        }
        throw new BadRequestException("Email already registered");
    }

    @GetMapping("/private/merchants")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<List<MerchantUser>> getAllMerchants() {
        User loggedInUser = userService.getLoggedInUser();
        List<Merchant> merchants = merchantService.findAllByState(loggedInUser.getState());

        return ResponseEntity.ok(merchants.stream().filter(merchant -> merchant.getUser().isStatus()).map(UserMapper::userToMerchant).collect(Collectors.toList()));
    }

    @GetMapping("/private/customers")
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    public ResponseEntity<List<CustomerUser>> getAllCustomersByActiveStates() {
        Merchant merchant = merchantService.getLoggedInMerchant();

        return ResponseEntity.ok(customerService.findAllByStatesAndStatus(new ArrayList<>(merchant.getActiveStates()), true).stream()
                .map(UserMapper::userToCustomer)
                .collect(Collectors.toList()));
    }

    @GetMapping("/private/announcements")
    public ResponseEntity<List<ReadableAnnouncement>> getAll(){
        List<Announcement> announcements = announcementService.findAllActives(new Date());
        return ResponseEntity.ok(announcements.stream()
                .map(AnnouncementMapper::announcementToReadableAnnouncement).collect(Collectors.toList()));
    }
}
