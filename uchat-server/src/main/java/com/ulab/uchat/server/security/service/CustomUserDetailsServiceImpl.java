package com.ulab.uchat.server.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ulab.uchat.server.dao.AuthDao;
import com.ulab.uchat.server.security.auth.UserAuthInfo;

@Component(value="CustomUserDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final AuthDao authDao;

    public CustomUserDetailsServiceImpl(AuthDao authMapper) {
        this.authDao = authMapper;
    }

    @Override
    public UserAuthInfo loadUserByUsername(String name) throws UsernameNotFoundException {
        UserAuthInfo userAuthInfo = authDao.findByUsername(name);
        if (userAuthInfo == null) {
            throw new UsernameNotFoundException(String.format("No userDetail found with username '%s'.", name));
        }
        return userAuthInfo;
    }
}
