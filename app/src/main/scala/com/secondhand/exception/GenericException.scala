package com.secondhand.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(reason = "Generic exception", value = HttpStatus.INTERNAL_SERVER_ERROR)
class GenericException(val message: String) extends RuntimeException(message) {
}