package com.secondhand.role;

import com.secondhand.user.User;

import lombok.Data;

import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoleDto {

    private long id;

    private String name;

    private List<User> users;
}
