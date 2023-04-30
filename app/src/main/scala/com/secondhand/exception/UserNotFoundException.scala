package com.secondhand.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(reason = "User not found", value = HttpStatus.NOT_FOUND)
class UserNotFoundException(val message: String) extends RuntimeException(message) {
}
