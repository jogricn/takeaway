package com.jogyco.takeaway.dao;

import com.jogyco.takeaway.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldFindEmployeeWithExistingEmail() throws ParseException {
        var employee = Employee.builder()
                .fullName("Name Lastname")
                .hobbies(List.of("Hobby 1", "Hobby 2"))
                .email("testemail@gmail.com")
                .birthday(LocalDate.parse("1980-12-12"))
                .build();

        employeeRepository.save(employee);

        var isTaken = employeeRepository.isEmailTaken("testemail@gmail.com");

        assertTrue(isTaken);
    }


}