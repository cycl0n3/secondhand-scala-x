package com.secondhand.security;

import com.secondhand.user.User;
import com.secondhand.user.UserService;

import lombok.AllArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOp;

        userOp = userService.getUserByUsername(username);

        if(userOp.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        userOp = userService.getUserByEmail(username);

        if(userOp.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        User user = userOp.get();

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName())).toList();

        // return spring security user
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
