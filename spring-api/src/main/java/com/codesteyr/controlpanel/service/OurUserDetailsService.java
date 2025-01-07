package com.codesteyr.controlpanel.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.codesteyr.controlpanel.repository.UsersRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OurUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(OurUserDetailsService.class);

    @Autowired
    private UsersRepo usersRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return usersRepo.findByEmail(username).orElseThrow(() -> {
            logger.error("User not found with email: {}", username);
            return new UsernameNotFoundException("User not found with email: " + username);
        });
    }
}
