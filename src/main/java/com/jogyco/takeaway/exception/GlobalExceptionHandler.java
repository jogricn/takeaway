package com.jogyco.takeaway.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    protected ResponseEntity<Object> handleEmployeeNotFound(
            EmployeeNotFoundException ex, WebRequest request) {


        return new ResponseEntity<>(ex.getMessage(), HttpStatusCode.valueOf(404));
    }

}
