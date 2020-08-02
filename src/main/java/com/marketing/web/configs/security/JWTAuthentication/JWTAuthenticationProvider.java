package com.marketing.web.configs.security.JWTAuthentication;

import com.marketing.web.configs.constants.ApplicationContstants;
import com.marketing.web.configs.security.CustomPrincipal;
import com.marketing.web.configs.security.JWTAuthToken.JWTAuthToken;
import com.marketing.web.configs.security.JWTAuthToken.JWTGenerator;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Customer;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;
import com.marketing.web.services.user.CustomerService;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.user.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JWTAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final UserService userService;

    private final CustomerService customerService;

    private final MerchantService merchantService;

    public JWTAuthenticationProvider(UserService userService, CustomerService customerService, MerchantService merchantService) {
        this.userService = userService;
        this.customerService = customerService;
        this.merchantService = merchantService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        JWTAuthToken jwtAuthenticationToken = (JWTAuthToken) authentication;
        String token = jwtAuthenticationToken.getToken();

        Claims body = (Claims) JWTGenerator.validate(token, ApplicationContstants.JWT_SECRET);
        String role = body.get("role").toString();
        RoleType roleType = RoleType.fromValue(role.split("_")[1]);
        if (role.isEmpty() || roleType == null) {
            throw new AuthenticationCredentialsNotFoundException("JWT Token is incorrect");
        }

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList(role);
        switch(roleType) {
            case CUSTOMER:
                Customer customer = customerService.findById(body.get("customerId").toString());
                return new CustomPrincipal(customer.getUser().getUsername(), customer.getUser().getId().toString(), token, grantedAuthorities,customer.getUser(), customer);
            case MERCHANT:
                Merchant merchant = merchantService.findById(body.get("merchantId").toString());
                return new CustomPrincipal(merchant.getUser().getUsername(), merchant.getUser().getId().toString(), token, grantedAuthorities,merchant.getUser(), merchant);
            default:
                User user = userService.findById(body.get("userId").toString());
                return new CustomPrincipal(user.getUsername(), user.getId().toString(), token, grantedAuthorities, user);
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (JWTAuthToken.class.isAssignableFrom(aClass));
    }

}