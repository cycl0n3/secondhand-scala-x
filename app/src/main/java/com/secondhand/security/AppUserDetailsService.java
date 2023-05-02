package com.secondhand.security;

import com.secondhand.user.User;
import com.secondhand.user.UserService;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: " + username);

        Optional<User> userByUsernameOp = userService.getUserByUsername(username);
        Optional<User> userByEmailOp = userService.getUserByEmail(username);

        if(userByUsernameOp.isEmpty() && userByEmailOp.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username or email: " + username);
        }

        Optional<User> userOp = userByUsernameOp.isPresent() ? userByUsernameOp : userByEmailOp;

        User user = userOp.get();

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName())).toList();

        log.info("returning user: " + user.getUsername());

        // return spring security user
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
