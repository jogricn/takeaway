package com.jogyco.takeaway.dao;

import com.jogyco.takeaway.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @Query("" +
            "SELECT CASE WHEN COUNT(e) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM Employee e " +
            "WHERE e.email = ?1"
    )
    Boolean isEmailTaken(String email);
}
