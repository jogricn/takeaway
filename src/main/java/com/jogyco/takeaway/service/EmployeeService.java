package com.jogyco.takeaway.service;

import com.jogyco.takeaway.EmailValidator;
import com.jogyco.takeaway.controller.EmployeeCreationRequest;
import com.jogyco.takeaway.controller.EmployeeUpdateRequest;
import com.jogyco.takeaway.dao.EmployeeRepository;
import com.jogyco.takeaway.exception.ApiRequestException;
import com.jogyco.takeaway.exception.EmployeeNotFoundException;
import com.jogyco.takeaway.kafka.EmployeeMessage;
import com.jogyco.takeaway.kafka.KafkaProducer;
import com.jogyco.takeaway.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final KafkaProducer kafkaProducer;

    private final EmailValidator emailValidator;

    public ResponseEntity<Employee> createEmployee(EmployeeCreationRequest employeeCreationRequest) {
        Employee employee = Employee.builder()
                .fullName(employeeCreationRequest.firstName() + " " + employeeCreationRequest.lastName())
                .email(employeeCreationRequest.email())
                .birthday(employeeCreationRequest.birthday())
                .hobbies(employeeCreationRequest.hobbies())
                .build();

        // check if email is valid & if it's not taken
        checkIfEmailIsGoodToGo(employee.getEmail());

        var savedEmployee = employeeRepository.save(employee);
        // Create Kafka message and send notification
        var employeeMessage = buildEmployeeMessage(savedEmployee, EmployeeMessage.Event.CREATED);
        //kafkaProducer.send("takeaway", savedEmployee.getId().toString(), employeeMessage);
        kafkaProducer.send(employeeMessage);

        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    public Employee findEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with id : [%s] not found!".formatted(id)));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(EmployeeUpdateRequest employee) {
        var id = employee.id();
        var email = employee.email();

        checkIfEmailIsGoodToGo(email);

        var employeeById = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with id : [%s] not found!".formatted(id)));

        employeeById.setBirthday(employee.birthday());
        employeeById.setEmail(email);
        employeeById.setHobbies(employee.hobbies());
        employeeById.setFullName(employee.firstName() + " " + employee.lastName());
        var updatedEmployee = employeeRepository.save(employeeById);

        var employeeMessage = buildEmployeeMessage(employeeById, EmployeeMessage.Event.UPDATED);
        kafkaProducer.send(employeeMessage);

        return updatedEmployee;
    }

    public void deleteEmployee(UUID id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with id : [%s] not found!".formatted(id)));

        employeeRepository.deleteById(id);

        // Create Kafka message and send notification
        var employeeMessage = buildEmployeeMessage(employee, EmployeeMessage.Event.DELETED);
        kafkaProducer.send(employeeMessage);
    }

    private void checkIfEmailIsGoodToGo(String email) {
        if (!emailValidator.test(email)) {
            throw new ApiRequestException(email + " is not valid email address!");
        } else if (employeeRepository.isEmailTaken(email)) {
            throw new ApiRequestException(email + " is taken!");
        }
    }

    private EmployeeMessage buildEmployeeMessage(Employee employee, EmployeeMessage.Event eventType) {
        return EmployeeMessage.builder()
                .id(employee.getId().toString())
                .employeeName(employee.getFullName())
                .hobbies(employee.getHobbies())
                .email(employee.getEmail())
                .birthday(employee.getBirthday())
                .event(eventType)
                .build();
    }
}
