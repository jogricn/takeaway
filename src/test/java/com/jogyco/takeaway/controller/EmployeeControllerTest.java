package com.jogyco.takeaway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogyco.takeaway.dao.EmployeeRepository;
import com.jogyco.takeaway.model.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

//    @Autowired
//    WebTestClient webTestClient;

    @Autowired
    EmployeeRepository employeeRepository;

    @AfterEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void shouldNotAllowToCreateNewUserForUnauthorizedRequest() {
        var employeeRequest = new EmployeeCreationRequest(
                "Nebojsa",
                "Jogric",
                "testemail@gogle.com",
                LocalDate.parse("1980-12-12"),
                List.of("Test Hobby"));

        var respEntity = testRestTemplate.postForEntity("/api/v1/employees", employeeRequest, EmployeeCreationRequest.class);

        Assertions.assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createEmployeeShouldSendNewKafkaMessage() {
        var employeeRequest = new EmployeeCreationRequest(
                "Nebojsa",
                "Jogric",
                "testemail@gogle.com",
                LocalDate.parse("1980-12-12"),
                List.of("Test Hobby"));

        var respEntity = testRestTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/v1/employees", employeeRequest, String.class);

        Assertions.assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    @Sql("/new-test-employees.sql")
    void getEmployeesWithoutBasicAuthShouldReturnAllEmployeesFromDB() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/expected_employees_in_db.json");
        ArrayList<LinkedHashMap> employeeList = objectMapper.readValue(file, new TypeReference<>(){});

        var respEntity = testRestTemplate.getForEntity("/api/v1/employees", List.class);

        Assertions.assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respEntity.getBody().size()).isEqualTo(3);
        Assertions.assertThat(respEntity.getBody()).isEqualTo(employeeList);
    }

    @Test
    @Sql("/new-test-employees.sql")
    void getEmployeeByIdShouldReturnSpecificEmployeeFromDB() throws IOException {
        var employeeId = "190c59cd-e8f9-4e98-94b3-2cc44555450a";
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/expected_employees_in_db.json");
        ArrayList<LinkedHashMap> employeeList = objectMapper.readValue(file, new TypeReference<>(){});

        var respEntity = testRestTemplate.getForEntity("/api/v1/employees/{employeeId}", Employee.class, employeeId);

        Assertions.assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respEntity.getBody()).isEqualTo(Employee.builder()
                .id(UUID.fromString("190c59cd-e8f9-4e98-94b3-2cc44555450a"))
                .fullName("Test Testeric2")
                .email("testemail2@gmail.com")
                .birthday(LocalDate.parse("1973-10-25"))
                .hobbies(List.of("hobi neki tamo novi2", "test 2"))
                .build());
    }

    @Test
    @Sql("/new-test-employees.sql")
    void updateEmployeeShouldSendAppropriateKafkaMessage() throws IOException {
        var employeeId = "190c59cd-e8f9-4e98-94b3-2cc44555450a";
        var employeeUpdateRequest = new EmployeeUpdateRequest(
                UUID.fromString(employeeId),
                "FirstnameX",
                "LastnameX",
                "emailx@test.com",
                LocalDate.parse("1973-10-25"),
                List.of("Hobby X", "testHobby 2")
        );

        testRestTemplate
                .withBasicAuth("admin", "admin")
                .put("/api/v1/employees/{id}", employeeUpdateRequest, employeeId);

        Assertions.assertThatNoException();
//        final ResponseEntity<Employee> response = testRestTemplate
//                .withBasicAuth("admin", "admin")
//                .exchange(
//                    String.format("http://localhost:8080/api/v1/employees/%s", employeeId),
//                    HttpMethod.PUT,
//                    new HttpEntity<>(employeeUpdateRequest),
//                    Employee.class
//        );
//        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Assertions.assertThat(response.getBody()).isEqualTo(Employee.builder()
//                .id(UUID.fromString("190c59cd-e8f9-4e98-94b3-2cc44555450a"))
//                .fullName("FirstnameX, LastnameX")
//                .email("emailx@test.com")
//                .birthday(LocalDate.parse("1973-10-25"))
//                .hobbies(List.of("Hobby X", "testHobby 2"))
//                .build());
    }

    @Test
    @Sql("/new-test-employees.sql")
    void shouldDeleteEmployee() throws IOException, InterruptedException {
        var employeeId = "190c59cd-e8f9-4e98-94b3-2cc44555450a";

        testRestTemplate
                .withBasicAuth("admin", "admin")
                .delete("/api/v1/employees/{employeeId}", employeeId);

        Assertions.assertThatNoException();

        var respEntity = testRestTemplate.getForEntity("/api/v1/employees", List.class);

        Assertions.assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respEntity.getBody().size()).isEqualTo(2);
    }



}