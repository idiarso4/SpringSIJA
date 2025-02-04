package com.school.masterdata.repository;

import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser(User user);
    Optional<Student> findByStudentNumber(String studentNumber);
}
