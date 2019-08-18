package com.marketing.web.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomPrincipal implements UserDetails {
    private String userName;
    private String token;
    private Long id;
    private String password;
    private User user;
    private Collection<GrantedAuthority> authorities;


    public CustomPrincipal(String userName, long id, String token, List<GrantedAuthority> grantedAuthorities) {

        this.userName = userName;
        this.id = id;
        this.token= token;
        this.authorities = grantedAuthorities;
    }

    public CustomPrincipal(String userName, long id, String token, List<GrantedAuthority> grantedAuthorities,User user) {

        this.userName = userName;
        this.id = id;
        this.token= token;
        this.authorities = grantedAuthorities;
        this.user = user;
    }

    public CustomPrincipal(User user) {
        this.user = user;
        this.userName = user.getUserName();
        this.id = user.getId();
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
        return userName;
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


    public String getUserName() {
        return userName;
    }

    public String getToken() {
        return token;
    }


    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}