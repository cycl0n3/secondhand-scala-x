package com.secondhand.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private long id;

    private String username;

    private String email;

    private String password;
}
