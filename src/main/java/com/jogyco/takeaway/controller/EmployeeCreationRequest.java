package com.jogyco.takeaway.controller;

import java.time.LocalDate;
import java.util.List;

public record EmployeeCreationRequest(
        String firstName,
        String lastName,
        String email,
        LocalDate birthday,
        List<String> hobbies) {
}
