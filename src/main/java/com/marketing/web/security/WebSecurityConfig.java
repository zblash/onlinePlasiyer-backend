package com.marketing.web.security;


import com.marketing.web.security.JWTAuthentication.JWTAuthEntryPoint;
import com.marketing.web.security.JWTAuthentication.JWTAuthenticationFilter;
import com.marketing.web.security.JWTAuthentication.JWTAuthenticationProvider;
import com.marketing.web.security.JWTAuthentication.JWTSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JWTAuthenticationProvider autheticationProvider;

    @Autowired
    private JWTAuthEntryPoint authEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(autheticationProvider));
    }

    @Bean
    public JWTAuthenticationFilter authTokenFilter() {

        JWTAuthenticationFilter filter =new JWTAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new JWTSuccessHandler());
        return filter;

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests().antMatchers("api/**").authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);


        http.headers().cacheControl();
    }

}
