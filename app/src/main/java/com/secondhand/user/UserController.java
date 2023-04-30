package com.secondhand.user;

import com.secondhand.exception.GenericException;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = new ArrayList<>();

        userService.getAllUsers().forEach(users::add);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/ex")
    public ResponseEntity<String> ex() throws GenericException {
        throw new GenericException("This is a generic exception from UserController.");
    }
}
