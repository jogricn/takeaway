package com.jogyco.takeaway.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {

    @Id
    @UuidGenerator
    private UUID id;
    private String fullName;
    private String email;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;
    private List<String> hobbies; //TODO: Maybe this should be predefined as e.g. enum and than chose a hobbies


}
