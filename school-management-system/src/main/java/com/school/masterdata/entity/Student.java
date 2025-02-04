package com.school.masterdata.entity;

import com.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "students")
@EqualsAndHashCode(callSuper = true)
public class Student extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "identification_number", nullable = false, unique = true)
    private String identificationNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "entry_year")
    private Integer entryYear;

    @ManyToOne
    @JoinColumn(name = "class_room_id")
    private ClassRoom classRoom;

}
