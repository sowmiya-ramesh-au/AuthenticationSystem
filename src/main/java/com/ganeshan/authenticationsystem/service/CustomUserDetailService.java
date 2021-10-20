package com.ganeshan.authenticationsystem.service;

import com.ganeshan.authenticationsystem.model.UserEntity;
import com.ganeshan.authenticationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.usernameExists(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        boolean enabled = user.isVerified();
        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!enabled)
                .authorities("USER")
                .build();
        return userDetails;
    }
}
