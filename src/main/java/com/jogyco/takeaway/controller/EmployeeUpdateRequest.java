package com.jogyco.takeaway.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EmployeeUpdateRequest(
        UUID id,
        String firstName,
        String lastName,
        String email,
        LocalDate birthday,
        List<String> hobbies) {
}
