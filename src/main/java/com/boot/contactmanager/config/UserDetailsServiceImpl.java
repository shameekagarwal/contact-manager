package com.boot.contactmanager.config;

import java.util.List;

import com.boot.contactmanager.daos.UserRepository;
import com.boot.contactmanager.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // spring security gives username
    // you have to retrieve the user corresponding to the username
    // for the project, username = email
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userRepository.getUsersByEmail(username);
        if (users.size() == 0) {
            throw new UsernameNotFoundException("account with entered email not found");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(users.get(0));
        return customUserDetails;
    }

}
