package com.secondhand.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(reason = "Role not found", value = HttpStatus.NOT_FOUND)
class RoleNotFoundException(val message: String) extends RuntimeException(message) {
}
