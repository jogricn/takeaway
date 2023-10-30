package com.jogyco.takeaway.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeMessage {
    //TODO: SOme unique value. Since all CREATED, UPDATED and DELETED will go on
    // the same topic, same employee Id can appear multiple time, but should be unique
    private String id;
    private String employeeName;
    private String email;
    private LocalDate birthday;
    private List<String> hobbies;
    private Event event;

    public enum Event {
        CREATED, UPDATED, DELETED
    }
}
