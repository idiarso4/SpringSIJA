package com.attendance.pkl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PKLAssignmentResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long supervisorId;
    private String supervisorName;
    private Long companyId;
    private String companyName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String position;
    private String department;
    private String status;
}
