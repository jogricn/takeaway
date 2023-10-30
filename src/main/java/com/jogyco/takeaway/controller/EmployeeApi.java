package com.jogyco.takeaway.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmployeeApi {

    @PostMapping
    @Operation(summary = "Create an employee",
            description = "Creates an employee for passed data and send notification about it on Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new employee")
    })
    ResponseEntity<?> createEmployee(@RequestBody EmployeeCreationRequest employeeCreationRequest);
}
