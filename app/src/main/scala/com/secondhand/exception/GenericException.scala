package com.secondhand.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

import org.slf4j.LoggerFactory

@ResponseStatus(reason = "Generic exception", value = HttpStatus.INTERNAL_SERVER_ERROR)
class GenericException(val message: String) extends Exception(message) {

    private val logger = LoggerFactory.getLogger(this.getClass)

    logger.error("Generic exception: " + message)
}

object GenericException {
    def apply(message: String): GenericException = new GenericException(message)
}
