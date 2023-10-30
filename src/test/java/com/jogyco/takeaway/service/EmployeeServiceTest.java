package com.jogyco.takeaway.service;

import com.jogyco.takeaway.EmailValidator;
import com.jogyco.takeaway.controller.EmployeeCreationRequest;
import com.jogyco.takeaway.controller.EmployeeUpdateRequest;
import com.jogyco.takeaway.dao.EmployeeRepository;
import com.jogyco.takeaway.exception.ApiRequestException;
import com.jogyco.takeaway.exception.EmployeeNotFoundException;
import com.jogyco.takeaway.kafka.KafkaProducer;
import com.jogyco.takeaway.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private static final String KAFKA_TOPIC = "takeaway";

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private EmailValidator emailValidator;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(
                employeeRepository,
                kafkaProducer,
                emailValidator);
    }

    @Test
    void shouldCreateNewEmployee() {
        var employeeId = UUID.randomUUID();
        var employeeEmail = "testemail@dot.com";
        var employeeCreationRequest = new EmployeeCreationRequest(
                "Nebojsa",
                "Jogric",
                employeeEmail,
                LocalDate.parse("1980-12-12"),
                List.of("Test Hobby")
        );

        var employee = Employee.builder()
                .id(employeeId)
                .fullName(employeeCreationRequest.firstName() + employeeCreationRequest.lastName())
                .email(employeeCreationRequest.email())
                .birthday(employeeCreationRequest.birthday())
                .hobbies(employeeCreationRequest.hobbies())
                .build();

        when(emailValidator.test(employeeEmail)).thenReturn(true);
        when(employeeRepository.isEmailTaken(employeeEmail)).thenReturn(false);
        when(employeeRepository.save(any())).thenReturn(employee);

        employeeService.createEmployee(employeeCreationRequest);

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());
        verify(kafkaProducer).send(any());

//        Employee value = employeeArgumentCaptor.getValue();
//
//        assertThat(value).isEqualTo(employee);
    }

    @Test
    void createEmployeeShouldThrowApiRequestExceptionForInvalidEmail() {
        var employeeEmail = "invaliemail.com";
        var employeeCreationRequest = new EmployeeCreationRequest(
                "Nebojsa",
                "Jogric",
                employeeEmail,
                LocalDate.parse("1980-12-12"),
                List.of("Test Hobby"));

        when(emailValidator.test(employeeEmail)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.createEmployee(employeeCreationRequest))
                .isInstanceOf(ApiRequestException.class)
                .hasMessageContaining("is not valid email address!");
        verify(employeeRepository, never()).save(any());
        verify(kafkaProducer, never()).send(any());
    }

    @Test
    void createEmployeeShouldThrowApiRequestExceptionIfEmailIsTaken() {
        var employeeEmail = "testemail@dot.com";
        var employeeCreationRequest = new EmployeeCreationRequest(
                "Nebojsa",
                "Jogric",
                employeeEmail,
                LocalDate.parse("1980-12-12"),
                List.of("Test Hobby"));

        when(emailValidator.test(employeeEmail)).thenReturn(true);
        when(employeeRepository.isEmailTaken(employeeEmail)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(employeeCreationRequest))
                .isInstanceOf(ApiRequestException.class)
                .hasMessageContaining("is taken!");
        verify(employeeRepository, never()).save(any());
        verify(kafkaProducer, never()).send(any());
    }


    @Test
    void shouldReturnExistingEmployeeForPassedId() {
        var employee = anEmployee();
        var employeeId = employee.getId();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        var foundedEmployee = employeeService.findEmployeeById(employeeId);

        assertThat(foundedEmployee).isEqualTo(employee);
    }

    @Test
    void findEmployeeByIdShouldThrowExceptionForNonExistingEmployee() {
        var employeeId = UUID.randomUUID();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findEmployeeById(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("not found!");

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void shouldReturnAllEmployees() {
        List<Employee> employeeList = List.of(anEmployee(), anEmployee("somenewemailaddress@google.com"));

        when(employeeRepository.findAll()).thenReturn(employeeList);

        var allEmployees = employeeService.getAllEmployees();

        verify(employeeRepository).findAll();

        assertThat(allEmployees.size()).isEqualTo(employeeList.size());
    }

    @Test
    void shouldUpdateEmployeeData() {
        var employeeUpdateRequest = anEmployeeUpdateRequest();
        var employeeEmail = employeeUpdateRequest.email();
        var employee = Employee.builder()
                .id(employeeUpdateRequest.id())
                .fullName(employeeUpdateRequest.firstName() + " " + employeeUpdateRequest.lastName())
                .email(employeeUpdateRequest.email())
                .birthday(employeeUpdateRequest.birthday())
                .hobbies(employeeUpdateRequest.hobbies())
                .build();

        when(employeeRepository.findById(employeeUpdateRequest.id())).thenReturn(Optional.of(employee));
        when(emailValidator.test(employeeEmail)).thenReturn(true);
        when(employeeRepository.isEmailTaken(employeeEmail)).thenReturn(false);

        employeeService.updateEmployee(employeeUpdateRequest);

        verify(employeeRepository).save(employee);
        verify(kafkaProducer).send(any());
    }

    @Test
    void updateEmployeeShouldThrowExceptionForNonExistingEmployee() {
        var employeeEmail = "testemail@dot.com";
        var employeeUpdateRequest = anEmployeeUpdateRequest(employeeEmail);

        when(emailValidator.test(employeeEmail)).thenReturn(true);
        when(employeeRepository.isEmailTaken(employeeEmail)).thenReturn(false);
        when(employeeRepository.findById(employeeUpdateRequest.id())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(employeeUpdateRequest))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("not found!");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployeeShouldThrowApiRequestExceptionForInvalidEmail() {
        var employeeEmail = "invaliemail.com";
        var employeeUpdateRequest = anEmployeeUpdateRequest(employeeEmail);

        when(emailValidator.test(employeeEmail)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.updateEmployee(employeeUpdateRequest))
                .isInstanceOf(ApiRequestException.class)
                .hasMessageContaining("is not valid email address!");

        verify(employeeRepository, never()).save(any());
        verify(kafkaProducer, never()).send(any());
    }

    @Test
    void updateEmployeeShouldThrowApiRequestExceptionIfEmailIsTaken() {
        var employeeEmail = "invalidemail.com";
        var employeeUpdateRequest = anEmployeeUpdateRequest(employeeEmail);

        when(emailValidator.test(employeeEmail)).thenReturn(true);
        when(employeeRepository.isEmailTaken(employeeEmail)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.updateEmployee(employeeUpdateRequest))
                .isInstanceOf(ApiRequestException.class)
                .hasMessageContaining("is taken!");
        verify(employeeRepository, never()).save(any());
        verify(kafkaProducer, never()).send(any());
    }

    @Test
    void shouldDeleteEmployee() {
        var employee = anEmployee();
        var employeeId = employee.getId();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository).deleteById(employeeId);
        verify(kafkaProducer).send(any());
    }

    @Test
    void deleteEmployeeShouldThrowExceptionForNonExistingEmployee() {
        var employeeId = UUID.randomUUID();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("not found!");
    }

    private EmployeeUpdateRequest anEmployeeUpdateRequest() {
        return new EmployeeUpdateRequest(
                UUID.randomUUID(),
                "Nebojsa",
                "Jogric",
                "testemail@dot.com",
                LocalDate.parse("1980-12-12"),
                List.of("Test Hobby", "Hobby 2"));
    }

    private EmployeeUpdateRequest anEmployeeUpdateRequest(String email) {
        return new EmployeeUpdateRequest(
                UUID.randomUUID(),
                "Nebojsa",
                "Jogric",
                email,
                LocalDate.parse("1980-12-12"),
                List.of("Test Hobby", "Hobby 2"));
    }

    private Employee anEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID())
                .fullName("Nebojsa Jogric")
                .email("testemail@dot.com")
                .birthday(LocalDate.parse("1980-12-12"))
                .hobbies(List.of("Test Hobby", "Hobby 2"))
                .build();
    }

    private Employee anEmployee(String email) {
        return Employee.builder()
                .id(UUID.randomUUID())
                .fullName("Nebojsa Jogric")
                .email(email)
                .birthday(LocalDate.parse("1980-12-12"))
                .hobbies(List.of("Test Hobby", "Hobby 2"))
                .build();
    }
}