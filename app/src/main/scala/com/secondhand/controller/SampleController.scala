package com.secondhand.controller

import com.secondhand.user.{User, UserService}

import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}

@RestController
@RequestMapping(Array("/api/v1/sample"))
class SampleController(
    private val userService: UserService,
) {

    @GetMapping(Array("/users"))
    def index(): java.util.List[User] = {
        userService.getAllUsers
    }
}
