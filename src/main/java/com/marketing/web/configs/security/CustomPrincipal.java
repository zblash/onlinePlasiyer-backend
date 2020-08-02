package com.marketing.web.configs.security;

import com.marketing.web.models.Customer;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomPrincipal implements UserDetails {
    private String username;
    private String token;
    private String id;
    private String password;
    private User user;
    private Customer customer;
    private Merchant merchant;
    private Collection<GrantedAuthority> authorities;


    public CustomPrincipal(String username, String id, String token, List<GrantedAuthority> grantedAuthorities) {

        this.username = username;
        this.id = id;
        this.token= token;
        this.authorities = grantedAuthorities;
    }

    public CustomPrincipal(String username, String id, String token, List<GrantedAuthority> grantedAuthorities,User user) {
        this.username = username;
        this.id = id;
        this.token= token;
        this.authorities = grantedAuthorities;
        this.user = user;
    }

    public CustomPrincipal(String username, String id, String token, List<GrantedAuthority> grantedAuthorities,User user, Customer customer) {
        this.username = username;
        this.id = id;
        this.token= token;
        this.authorities = grantedAuthorities;
        this.user = user;
        this.customer = customer;
    }

    public CustomPrincipal(String username, String id, String token, List<GrantedAuthority> grantedAuthorities,User user, Merchant merchant) {
        this.username = username;
        this.id = id;
        this.token= token;
        this.authorities = grantedAuthorities;
        this.user = user;
        this.merchant = merchant;
    }

    public CustomPrincipal(User user) {
        this.user = user;
        this.username = user.getUsername();
        this.id = user.getId().toString();
        this.password = user.getPassword();
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getToken() {
        return token;
    }


    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}