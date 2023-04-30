package com.secondhand.user;

import com.secondhand.role.Role;

import lombok.Data;

import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {

    private long id;

    private String username;

    private String email;

    private String password;

    private List<Role> roles;
}
