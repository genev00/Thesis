package com.genev.a100nts.server.security;

import com.genev.a100nts.server.exceptions.ClientException;
import com.genev.a100nts.server.models.UserRole;
import com.genev.a100nts.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] NOT_SECURED_ENDPOINTS;
    private static final String[] ADMIN_ONLY_ENDPOINTS;

    static {
        NOT_SECURED_ENDPOINTS = new String[]{
                "/api/v1/*/public/**"
        };
        ADMIN_ONLY_ENDPOINTS = new String[]{
                "/api/v1/admin/**"
        };
    }

    @Autowired
    private UserRepository userRepository;

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return ((User) principal).getUsername();
            }
        }
        throw new ClientException("There is no authorized user");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(NOT_SECURED_ENDPOINTS).permitAll()
                .antMatchers(ADMIN_ONLY_ENDPOINTS).hasRole(UserRole.ADMIN.name())
                .anyRequest().hasAnyRole(UserRole.USER.name(), UserRole.ADMIN.name())
                .and()
                .httpBasic();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Optional<com.genev.a100nts.server.models.User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new UsernameNotFoundException(email);
            }
            com.genev.a100nts.server.models.User user = optionalUser.get();
            return User.withUsername(user.getEmail()).password(user.getPassword()).roles(user.getRole().name()).build();
        };
    }

}
