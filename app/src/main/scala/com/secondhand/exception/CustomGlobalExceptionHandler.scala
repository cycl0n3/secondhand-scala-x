package com.secondhand.exception

import org.springframework.http.{HttpHeaders, HttpStatusCode, ResponseEntity}
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import java.util

@RestControllerAdvice
class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    override def handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity[AnyRef] = {
        val body = new util.HashMap[String, Any]()

        body.put("timestamp", java.time.LocalDateTime.now())
        body.put("status", status.value())

        val errors = ex.getBindingResult.getFieldErrors.stream()
            .map(x => x.getField + ": " + x.getDefaultMessage)
            .toList

        body.put("errors", errors)

        new ResponseEntity(body, headers, status)
    }
}