package com.jogyco.takeaway.controller;


import com.jogyco.takeaway.model.Employee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface EmployeeApi {

    @PostMapping
    @Operation(summary = "Create an employee",
            description = "Creates an employee for passed data and send notification about it on Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new employee")
    })
    ResponseEntity<?> createEmployee(@RequestBody EmployeeCreationRequest employeeCreationRequest);


    @GetMapping
    @Operation(summary = "Get all employees", description = "Returning a ist of all employees in JSON Array format")
    List<Employee> getAllEmployees();


    @GetMapping(path = "{id}")
    @Operation(summary = "Get employee by Id", description = "Returning a specific employee by uuid - response in JSON Object format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully pulled out employee deteils"),
            @ApiResponse(responseCode = "404", description = "Employee with given Id not found")
    })
    public Employee getEmployeeById(@PathVariable UUID id);


    @DeleteMapping(path = "{id}")
    @Operation(summary = "Delete employee by Id", description = "Delete a specific employee with provided uuid")
    public void deleteEmployeeById(@PathVariable("id") UUID id);


    @PutMapping
    @Operation(summary = "Update employee", description = "Update employee ...")
    public void updateEmployee(@RequestBody EmployeeUpdateRequest employee);
}
