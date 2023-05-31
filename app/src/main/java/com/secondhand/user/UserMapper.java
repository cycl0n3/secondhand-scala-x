package com.secondhand.user;

import com.secondhand.role.Role;

import com.secondhand.role.RoleDto;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setPassword("********");
        userDto.setEnabled(user.isEnabled());

        if(user.getPicture() != null) {
            userDto.setPicture(Base64.getEncoder().encodeToString(user.getPicture()));
        } else {
            userDto.setPicture(null);
        }

        List<RoleDto> roles = user.getRoles().stream().map(role -> {
            RoleDto roleDto = new RoleDto();
            roleDto.setId(role.getId());
            roleDto.setName(role.getName());
            return roleDto;
        }).toList();

        userDto.setRoles(roles);

        return userDto;
    }

    public List<UserDto> toUserDto(List<User> users) {
        return users.stream().map(this::toUserDto).toList();
    }

    public User toUser(UserDto userDto) {
        User user = new User();

        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setEnabled(userDto.isEnabled());

        if(userDto.getPicture() != null) {
            user.setPicture(Base64.getDecoder().decode(userDto.getPicture()));
        } else {
            user.setPicture(null);
        }

        List<Role> roles = userDto.getRoles().stream().map(roleDto -> {
            Role role = new Role();
            role.setId(roleDto.getId());
            role.setName(roleDto.getName());
            return role;
        }).toList();

        user.setRoles(roles);

        return user;
    }
}
